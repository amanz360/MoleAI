// ******************************************************* 
//                   MACHINE GENERATED CODE                
//                MUST BE CAREFULLY COMPLETED              
//                                                         
//           ABSTRACT METHODS MUST BE IMPLEMENTED          
//                                                         
// Generated on 04/24/2017 22:41:14
// ******************************************************* 
package bts.conditions.execution;

import bwapi.Position;
import moleAI.MoleUnit;
import moleAI.Squad;

/** ExecutionCondition class created from MMPM condition AwayFromSquad. */
public class AwayFromSquad extends
		jbt.execution.task.leaf.condition.ExecutionCondition {

	/**
	 * Constructor. Constructs an instance of AwayFromSquad that is able to run
	 * a bts.conditions.AwayFromSquad.
	 */
	public AwayFromSquad(bts.conditions.AwayFromSquad modelTask,
			jbt.execution.core.BTExecutor executor,
			jbt.execution.core.ExecutionTask parent) {
		super(modelTask, executor, parent);

	}

	protected void internalSpawn() {
		/*
		 * Do not remove this first line unless you know what it does and you
		 * need not do it.
		 */
		this.getExecutor().requestInsertionIntoList(
				jbt.execution.core.BTExecutor.BTExecutorList.TICKABLE, this);
		/* TODO: this method's implementation must be completed. */
		System.out.println(this.getClass().getCanonicalName() + " spawned");
		MoleUnit currentEntity = (MoleUnit) this.getContext().getVariable("CurrentEntity");
		Squad mySquad = (Squad) this.getContext().getVariable("squad");
		Position squadCenter = mySquad.getCenterOfSquad();
		//System.out.println("Got squad center: " + squadCenter);
		if(currentEntity.myUnit.getDistance(squadCenter) > 100)
		{
			this.getContext().setVariable("tooFar", true);
			this.getContext().setVariable("squadCenter", squadCenter);
			//System.out.println("Set squad center");
		}
		else
		{
			//System.out.println("Close to squad!");
			this.getContext().setVariable("tooFar", false);
		}
	}

	protected jbt.execution.core.ExecutionTask.Status internalTick() {
		/*
		 * TODO: this method's implementation must be completed. This function
		 * should only return Status.SUCCESS, Status.FAILURE or Status.RUNNING.
		 * No other values are allowed.
		 */
		if((boolean)this.getContext().getVariable("tooFar") == true)
		{
			return jbt.execution.core.ExecutionTask.Status.SUCCESS;
		}
		else
		{
			return jbt.execution.core.ExecutionTask.Status.FAILURE;
		}
	}

	protected void internalTerminate() {
		/* TODO: this method's implementation must be completed. */
	}

	protected void restoreState(jbt.execution.core.ITaskState state) {
		/* TODO: this method's implementation must be completed. */
	}

	protected jbt.execution.core.ITaskState storeState() {
		/* TODO: this method's implementation must be completed. */
		return null;
	}

	protected jbt.execution.core.ITaskState storeTerminationState() {
		/* TODO: this method's implementation must be completed. */
		return null;
	}
}