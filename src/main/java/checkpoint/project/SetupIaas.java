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

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hu.mta.sztaki.lpds.cloud.simulator.energy.powermodelling.PowerState;
import hu.mta.sztaki.lpds.cloud.simulator.examples.jobhistoryprocessor.VMKeeper;
import hu.mta.sztaki.lpds.cloud.simulator.helpers.job.Job;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.IaaSService;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.PhysicalMachine;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.VMManager.VMManagementException;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.VirtualMachine;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.constraints.ResourceConstraints;
import hu.mta.sztaki.lpds.cloud.simulator.io.NetworkNode.NetworkException;
import hu.mta.sztaki.lpds.cloud.simulator.io.NetworkNode.State;
import hu.mta.sztaki.lpds.cloud.simulator.io.Repository;
import hu.mta.sztaki.lpds.cloud.simulator.io.StorageObject;
import hu.mta.sztaki.lpds.cloud.simulator.io.VirtualAppliance;
import hu.mta.sztaki.lpds.cloud.simulator.util.PowerTransitionGenerator;
import checkpoint.project.ExercisesBaseProj;

public class SetupIaas {
	
	private CPSingleJobRunner SJR;
	private ExercisesBaseProj base;
	private static VMKeeper[] keeper;
	private static Job job;
	
public static void main (String[] args) throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, NoSuchFieldException, VMManagementException, NetworkException {

	//getting infrastructure
	IaaSService gettingIaas = (IaaSService) ExercisesBaseProj.getComplexInfrastructure(1);
	//List<Repository> repo = gettingIaas.repositories;
	
	
	System.out.println(gettingIaas);
	
	
	//getting arguments to request a VM
	VirtualAppliance machine = new VirtualAppliance("AD1", 1.5, 10);
	ResourceConstraints capacity = gettingIaas.getCapacities();
	
	
	
	//setting up repository
	Map<String, Integer> latency = new HashMap<String, Integer>(16);
	
	String state = null;
	
	Map<String, PowerState> diskPower = new HashMap<String, PowerState>();
	Map<String, PowerState> networkPower = new HashMap<String, PowerState>();
	
	PowerState thisDiskPower = PowerTransitionGenerator.getPowerStateFromMap(diskPower, state);
	PowerState thisNetworkPower = PowerTransitionGenerator.getPowerStateFromMap(networkPower, state);
	
	
	Map<String, PowerState> diskPower2 = (Map<String, PowerState>) thisDiskPower;
	Map<String, PowerState> networkPower2 = (Map<String, PowerState>) thisNetworkPower;
	
	
	
	Repository repo = new Repository(100, "AD2", 20, 20, 25, latency, diskPower2, networkPower2);
	
	VirtualMachine[] requesting = gettingIaas.requestVM(machine, capacity, repo, 3);
	
	VirtualMachine thisMachine = (VirtualMachine) Array.get(requesting, 0);
	
	IaaSService selectIaas = gettingIaas; //looking at list of vms
	VirtualMachine request = thisMachine; //choosing vm
	long bill = 2;
	
	getKeeper(selectIaas, request, bill);
	//VMKeeper[] aaronKeeper = getKeeper(selectIaas, request, bill);
	//Job thisJob = getJob(job);

	//CheckpointSingleJobRunner s = new CheckpointSingleJobRunner(getJob(job), getKeeper(selectIaas, request, bill));
	}

	
	
	public SetupIaas()  {
		
		
	}
	
	public static Job getJob(Job job) {
		return getJob(job);
	}
	
	public static VMKeeper[] getKeeper(IaaSService selectIaas, VirtualMachine request, long bill) {
		System.out.println(selectIaas);
		System.out.println(request);
		System.out.println(bill);
		return keeper; 
		//getKeeper(selectIaas, request, bill);
		
	}
	
	
//	private void getKeeper (IaaSService selectIaas, VirtualMachine request, long bill) {
//		return;
//	}
	

}
