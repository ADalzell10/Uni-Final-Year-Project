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

import hu.mta.sztaki.lpds.cloud.simulator.examples.jobhistoryprocessor.VMKeeper;
import hu.mta.sztaki.lpds.cloud.simulator.helpers.job.Job;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.IaaSService;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.VMManager.VMManagementException;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.VirtualMachine;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.constraints.ResourceConstraints;
import hu.mta.sztaki.lpds.cloud.simulator.io.NetworkNode.NetworkException;
import hu.mta.sztaki.lpds.cloud.simulator.io.Repository;
import hu.mta.sztaki.lpds.cloud.simulator.io.VirtualAppliance;
import hu.unimiskolc.iit.distsys.ExercisesBase;

public class SetupIaas {
	
	private CPSingleJobRunner SJR;
	private ExercisesBase base;
	private VMKeeper[] keeper;
	private static Job job;
	
	
	
	public SetupIaas() throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, NoSuchFieldException, VMManagementException, NetworkException {
		
		IaaSService gettingIaas = (IaaSService) ExercisesBase.getComplexInfrastructure(1);
		
		VirtualAppliance machine = (VirtualAppliance) gettingIaas.machines;
		ResourceConstraints capacity = gettingIaas.getCapacities();
		Repository repo = (Repository) gettingIaas.repositories;
		
		VirtualMachine[] requesting = gettingIaas.requestVM(machine, capacity, repo, 3);
		
		VirtualMachine thisMachine = (VirtualMachine) Array.get(requesting, 0);
		
		IaaSService selectIaas = gettingIaas; //looking at list of vms
		VirtualMachine request = thisMachine; //choosing vm
		long bill = 2;
		
	
		VMKeeper[] aaronKeeper = getKeeper(selectIaas, request, bill);
		Job thisJob = getJob(job);
	
		//CheckpointSingleJobRunner s = new CheckpointSingleJobRunner(getJob(job), getKeeper(selectIaas, request, bill));
		
		
	}
	
	public static Job getJob(Job job) {
		return getJob(job);
	}
	
	public static VMKeeper[] getKeeper(IaaSService selectIaas, VirtualMachine request, long bill) {
		return getKeeper(selectIaas, request, bill);
	}
	
	
//	private void getKeeper (IaaSService selectIaas, VirtualMachine request, long bill) {
//		return;
//	}
	

}
