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


import hu.mta.sztaki.lpds.cloud.simulator.examples.jobhistoryprocessor.VMKeeper;
import hu.mta.sztaki.lpds.cloud.simulator.helpers.job.Job;


public class Checkpoint {

	private static Job job;
	
	public Checkpoint(Job CP) {
		job = CP;
		System.out.println("Checkpoint saved.");
		
	}
	
	public static void loadCheckpoint() {
		//loads most recent checkpoint
		System.out.println("\nLoading checkpoint.");
		
		System.out.println("Checkpoint held: " + job);
		System.out.println("Checkpointed Job Id: " + job.getId() + ", Current Job Id: " + TestScenario2.id);
		//System.out.println(TestScenario2.id);
		
		if (job.getId() == TestScenario2.id) {
			VMKeeper[] newKeeper = TestScenario2.keeperSetup(TestScenario2.iaas,  
					TestScenario2.request,  TestScenario2.bill);
			
			
			//new CPSingleJobRunnerTestDestroy(job, cc);
			new CPSingleJobRunner(job, newKeeper);
		} else {
		System.out.print("Can't load checkpoint for a different job!");
		}
	}
	
}
