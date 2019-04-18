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

public class CPSingleJobRunnerTestDestroy implements VirtualMachine.StateChange, ConsumptionEvent {
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
		
		//Timed.simulateUntil(100000);
		
		// gets vmkeeper and job from setup class
		//SetupIaaS.jobDetails();
		
		
		TestScenario1.jobDetails();
		//TestScenario2.jobDetails();
		//TestScenario3.jobDetails();
		
	}
	
	public CPSingleJobRunnerTestDestroy(final Job runMe, final VMKeeper[] onUs) {
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
				//stateChanged(vmSet[i], vmSet[i].getState(), VirtualMachine.State.RUNNING);
				System.out.println(vmSet[i].getState());
			}
		}
		
		
		//stateChanged(keeperSet, null, null);
		
		
		System.out.println("running");
		//System.out.println(toProcess);
		//takeCheckpoint();
		
		//Timed.simulateUntil(1000000);
		
		
		startProcess();
	}
	
	public CPSingleJobRunnerTestDestroy() {
		
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

	private void startProcess() {
		
		System.out.println("process started");
		
		if (readyVMCounter == vmSet.length) {
			// Mark that we start the job / no further queuing
			System.out.println("if statement reached");
			
			toProcess.started();
			
			timeout.cancel();
			try {
				// vmset could get null if the compute task is rapidly terminating!
				for (int i = 0; vmSet != null && i < vmSet.length; i++) {
					
					
					// run the job's relevant part in the VM
					//resource consumption returned necessary for suspending job
					
					ResourceConsumption resCon = vmSet[i].newComputeTask(
							toProcess.getExectimeSecs() * vmSet[i].getResourceAllocation().allocated.getRequiredCPUs(),
							ResourceConsumption.unlimitedProcessing, this);
					
					System.out.println("test");
					//stores the consumption globally;
					this.rc = resCon;
					
					
					
					
				} 
			} catch (Exception e) {
				System.err.println(
						"Unexpected network setup issues while trying to send a new compute task to one of the VMs supporting job processing");
				e.printStackTrace();
				System.exit(1);
			}
			
//			
//			rc.suspend();
//			System.out.println("job suspended");
			
			//saves checkpoint of job
			takeCheckpoint();
			
			//job resumed
//			rc.registerConsumption();
//			System.out.println("resumed");
			
			
			// potential way of testing the load checkpoint action
			
		
			rc.cancel();
			
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
	
	
	
	
}
