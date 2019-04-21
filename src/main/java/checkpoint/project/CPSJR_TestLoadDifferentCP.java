/*
 *  ========================================================================
 *  DISSECT-CF Examples
 *  ========================================================================
 *  
 *  This file is part of DISSECT-CF Examples.
 *  
 *  DISSECT-CF Examples is free software: you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License as published
 *  by the Free Software Foundation, either version 3 of the License, or (at
 *  your option) any later version.
 *  
 *  DISSECT-CF Examples is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of 
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along
 *  with DISSECT-CF Examples.  If not, see <http://www.gnu.org/licenses/>.
 *  
 *  (C) Copyright 2015, Gabor Kecskemeti (kecskemeti.gabor@sztaki.mta.hu)
 */
package checkpoint.project;

import java.lang.reflect.InvocationTargetException;
import hu.mta.sztaki.lpds.cloud.simulator.DeferredEvent;
import hu.mta.sztaki.lpds.cloud.simulator.Timed;
import hu.mta.sztaki.lpds.cloud.simulator.examples.jobhistoryprocessor.VMKeeper;
import hu.mta.sztaki.lpds.cloud.simulator.helpers.job.Job;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.VMManager.VMManagementException;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.VirtualMachine;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.resourcemodel.ResourceConsumption;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.resourcemodel.ResourceConsumption.ConsumptionEvent;
import hu.mta.sztaki.lpds.cloud.simulator.io.NetworkNode.NetworkException;

public class CPSJR_TestLoadDifferentCP extends Timed implements VirtualMachine.StateChange, ConsumptionEvent {
	public static final long defaultStartupTimeout = 24 * 3600000; // a day
	public static final long startupTimeout;
	

	static {
		String to = System.getProperty("hu.mta.sztaki.lpds.cloud.simulator.examples.startupTimeout");
		startupTimeout = to == null ? defaultStartupTimeout : Long.parseLong(to);
		//System.err.println("VM startup timeout is set to " + startupTimeout);
	}
	private ResourceConsumption rc;
	
	
	private Job toProcess;
	private VMKeeper[] keeperSet;
	private VirtualMachine[] vmSet;
	private int readyVMCounter = 0;
	private int completionCounter = 0;
	private DeferredEvent timeout = new DeferredEvent(startupTimeout) {

		@Override
		protected void eventAction() {
			// After our timeout we still don't have all VMs started, we just forget about
			// this job
			releaseVMset();
		}
	};
	
	public static void main (String[] args) throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, NoSuchFieldException, VMManagementException, NetworkException {
		
		// gets vmkeeper and job from setup class
		TestScenario2.jobDetails();
			
	}
	
	public CPSJR_TestLoadDifferentCP(final Job runMe, final VMKeeper[] onUs) {
		toProcess = runMe;
		keeperSet = onUs;
		vmSet = new VirtualMachine[keeperSet.length];
		
		// Ensuring we receive state dependent events about the new VMs
		for (int i = 0; i < keeperSet.length; i++) {
			vmSet[i] = keeperSet[i].acquire();
			
			if (VirtualMachine.State.RUNNING.equals(vmSet[i].getState())) {
				
				readyVMCounter++;
			} else {
				
				vmSet[i].subscribeStateChange(this);		
			}
		}
		startProcess();
	}
	
	public CPSJR_TestLoadDifferentCP() {
		
	}

	@Override
	public void stateChanged(final VirtualMachine vm, final VirtualMachine.State oldState,
			final VirtualMachine.State newState) {
		
		// Now to the real business of having a VM that is actually capable of
		// running the job
		if (newState.equals(VirtualMachine.State.RUNNING)) {
			// Ensures that jobs intended for parallel execution are really run
			// in parallel
			++readyVMCounter;
			startProcess();
		}
	}

	private void startProcess()  {
		
		System.out.println("Attempting to run VM");
		
		if (readyVMCounter == vmSet.length) {
			// Mark that we start the job / no further queuing
			System.out.println("VMs Running.");
			
			toProcess.started();
			
			timeout.cancel();
			try {
				// vmset could get null if the compute task is rapidly terminating!
				for (int i = 2; vmSet != null && i < vmSet.length; i++) {
					
					// run the job's relevant part in the VM
					//resource consumption returned necessary for suspending job
					ResourceConsumption resCon = vmSet[i].newComputeTask(
							toProcess.getExectimeSecs() * vmSet[i].getResourceAllocation().allocated.getRequiredCPUs(),
							ResourceConsumption.unlimitedProcessing, this);
					
					//stores the consumption globally;
					this.rc = resCon;
					
				} 
			} catch (Exception e) {
				System.err.println(
						"Unexpected network setup issues while trying to send a new compute task to one of the VMs supporting job processing");
				e.printStackTrace();
				System.exit(1);
			}
			
			
			//cancels consumption
			rc.cancel();
			
			//calls to load checkpoint as consumption is cancelled
//			if(rc.isResumable() == false) {
//			loadCheckpoint();
//			}
			
			if(rc.isResumable() == false) {
				loadCheckpoint();
				}
			
		
			
		} 
	}
	
	/**
	 * Event handler for the completion of the job
	 */
	@Override
	public void conComplete() {

		if (++completionCounter == vmSet.length) {
			// everything went smoothly we mark it in the job
			toProcess.completed();
			
			System.out.println("Job Executed Successfully.");
			
			// the VMs are no longer needed
			releaseVMset();
			
			vmSet = null;
			toProcess = null;
			
		}
	}

	private void releaseVMset() {
		for (int i = 0; i < vmSet.length; i++) {
			vmSet[i].unsubscribeStateChange(this);
			keeperSet[i].release(vmSet[i]);
			keeperSet[i] = null;
			vmSet[i] = null;
		}
	}

	@Override
	public void conCancelled(ResourceConsumption problematic) {
		// just ignore this has happened :)
		// In the current setup we are not supposed to have failing jobs
	}
	
	// saving and loading of checkpoint
	public void takeCheckpoint() {
		System.out.println("Interrupting for checkpoint.");
		
		new Checkpoint(toProcess);	
		}
	
	public void loadCheckpoint() {
		Checkpoint.loadCheckpoint();
	}
	

	@Override
	public void tick(long fires) {
		
	}
}
