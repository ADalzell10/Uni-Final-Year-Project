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

import hu.mta.sztaki.lpds.cloud.simulator.Timed;
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
import checkpoint.project.ExercisesBaseProj;

public class TestScenario2 {
	
	public static VirtualMachine request;
	public static IaaSService iaas;
	public static long bill = 6000;
	
	//this is the main issue used for testing if a checkpoint
	//can be loaded for a different job
	public static String id = "1003";
	
	
	
	public static void jobDetails() throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, NoSuchFieldException, VMManagementException, NetworkException {
	
	//getting infrastructure
	IaaSService gettingIaas = (IaaSService) ExercisesBaseProj.getComplexInfrastructure(1);
	
	//getting arguments to request a VM
	//creating VA
	final long VAsize = 856800000;
	VirtualAppliance appliance = new VirtualAppliance("AD1", 0, 10, true, VAsize * 20);
    
	//resource constraint for each VM
	ConstantConstraints constraint = new ConstantConstraints(gettingIaas.getCapacities().getRequiredCPUs() / 3, 
			gettingIaas.getCapacities().getRequiredProcessingPower() / 3, 10000000);

	ResourceConstraints capacity = constraint;
	
	//using repository from iaas to store vms
	Repository repo = gettingIaas.repositories.get(0);
	gettingIaas.registerRepository(repo);
	
	repo.registerObject(appliance);
	
	
	//vm request
	VirtualMachine[] requesting = gettingIaas.requestVM(appliance, capacity, repo, 3);
	
	
	//details for vmkeeper
	VirtualMachine vm = (VirtualMachine) Array.get(requesting, 0);
	iaas = gettingIaas; 		//iaas
	request = vm; 			//choosing vm
	//long bill = 60000;						//bill in milliseconds
	
	
	
	//instantiating job
	DCFJob thisJob = new DCFJob(id, 100, 0, 200, 10, 5, 1000, "test","Test2", "Testing", null, 4);
	Job newJob = thisJob;
	
	
	VMKeeper[] newKeeper = keeperSetup(iaas,  request,  bill);		//vmkeeper

	new CPSingleJobRunnerTestDestroy(newJob, newKeeper);						//call to begin executing job
	
	Timed.simulateUntil(10000000);
	
	}
	
	public TestScenario2() {
		
	}

	//places vm details into an array to be used by CPSingleJobRunner
	public static VMKeeper[] keeperSetup(IaaSService iaas, VirtualMachine request, long bill) {
		int count = 3;
		VMKeeper[] vms = new VMKeeper[count];
		for (int i = 0; i < count ; i++) {
			vms[i] = new VMKeeper(iaas, request, bill);
		}
		return vms; 	
	}
	
	
	
	
	
	
	
}
