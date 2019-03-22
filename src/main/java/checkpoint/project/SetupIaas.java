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
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import hu.mta.sztaki.lpds.cloud.simulator.energy.powermodelling.PowerState;
import hu.mta.sztaki.lpds.cloud.simulator.examples.jobhistoryprocessor.DCFJob;
import hu.mta.sztaki.lpds.cloud.simulator.examples.jobhistoryprocessor.VMKeeper;
import hu.mta.sztaki.lpds.cloud.simulator.helpers.job.Job;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.IaaSService;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.VMManager.VMManagementException;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.VirtualMachine;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.constraints.ConstantConstraints;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.constraints.ResourceConstraints;
import hu.mta.sztaki.lpds.cloud.simulator.io.NetworkNode.NetworkException;
import hu.mta.sztaki.lpds.cloud.simulator.io.Repository;
import hu.mta.sztaki.lpds.cloud.simulator.io.VirtualAppliance;
import hu.mta.sztaki.lpds.cloud.simulator.util.PowerTransitionGenerator;
import checkpoint.project.ExercisesBaseProj;

public class SetupIaas {
	
	private static VMKeeper[] keeper;
	private static Job job;
		
	public static void jobDetails() throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, NoSuchFieldException, VMManagementException, NetworkException {
	
	//getting infrastructure
	IaaSService gettingIaas = (IaaSService) ExercisesBaseProj.getComplexInfrastructure(1);
	
	
	//getting arguments to request a VM
	VirtualAppliance appliance = new VirtualAppliance("AD1", 1.5, 10);
	
	ConstantConstraints constraint = new ConstantConstraints(5, 50000, 100000);
	ResourceConstraints capacity = constraint;
	
	
	//setting up repository
	Map<String, Integer> latency = new HashMap<String, Integer>(16);
	
	final EnumMap<PowerTransitionGenerator.PowerStateKind, Map<String, PowerState>> state = PowerTransitionGenerator.generateTransitions(20, 296, 493, 50, 108);
	
	final Map<String, PowerState> diskPower = state.get(PowerTransitionGenerator.PowerStateKind.storage);		//disk power
	final Map<String, PowerState> networkPower = state.get(PowerTransitionGenerator.PowerStateKind.network);	//network power
	
	Repository repo = new Repository(1000000, "AD2", 2000, 2000, 3500, latency, diskPower, networkPower);
	
	//vm request
	VirtualMachine[] requesting = gettingIaas.requestVM(appliance, capacity, repo, 3);
	
	//setting up vmkeeper
	VirtualMachine vm = (VirtualMachine) Array.get(requesting, 0);
	IaaSService selectIaas = gettingIaas; 	//iaas
	VirtualMachine request = vm; 			//choosing vm
	long bill = 10000;						//bill in milliseconds
	
	
	//instantiating job
	DCFJob thisJob = new DCFJob("1001", 100, 5, 200, 10, 5, 1000, "Aaron","client", "exec", null, 4);
	Job newJob = thisJob;
	
	
	VMKeeper[] newKeeper = Keeper(selectIaas,  request,  bill);		//vmkeeper

	new CPSingleJobRunner(newJob, newKeeper);						//call to begin executing job

	}
	
	public SetupIaas() {
		
	}

	//places vm details into an array to be used by CPSingleJobRunner
	public static VMKeeper[] Keeper(IaaSService selectIaas, VirtualMachine request, long bill) {
		int count = 5;
		VMKeeper[] vms = new VMKeeper[count];
		for (int i = 0; i < count ; i++) {
			vms[i] = new VMKeeper(selectIaas, request, bill);
		}
		return vms; 	
	}
	
	
	public void setupJob (String choice) {
		if (choice.equals("yes")) {
			System.out.println("Job submitted");
			
			//new CPSingleJobRunner(job, keeper);
			
			CPSingleJobRunner ss = new CPSingleJobRunner();
			ss.beginJob();
		}
	}
}
