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


public class Checkpoint {
	

	static CPSingleJobRunner s = new CPSingleJobRunner();


	public static void main (String[] args) {

		s.beginJob();

		//checkpoint begins
		System.out.println("Saving Checkpoint");


	}


	public static void saveCheckpoint() {

		System.out.println();
		System.out.println("\nCheckpoint Saved");
		   
		jobReturn();

	}

	private static void jobReturn() {

		System.out.println("\nResuming job execution");

		// call to job runner class


	}
}
