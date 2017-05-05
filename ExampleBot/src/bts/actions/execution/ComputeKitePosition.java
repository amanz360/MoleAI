// ******************************************************* 
//                   MACHINE GENERATED CODE                
//                MUST BE CAREFULLY COMPLETED              
//                                                         
//           ABSTRACT METHODS MUST BE IMPLEMENTED          
//                                                         
// Generated on 04/25/2017 16:07:41
// ******************************************************* 
package bts.actions.execution;

import bwapi.Position;
import bwapi.PositionOrUnit;
import bwapi.Unit;
import moleAI.MoleUnit;

/** ExecutionAction class created from MMPM action ComputeKitePosition. */
public class ComputeKitePosition extends
		jbt.execution.task.leaf.action.ExecutionAction {

	/**
	 * Constructor. Constructs an instance of ComputeKitePosition that is able
	 * to run a bts.actions.ComputeKitePosition.
	 */
	public ComputeKitePosition(bts.actions.ComputeKitePosition modelTask,
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
		Unit target = currentEntity.myTarget.getUnit();
		int fleeX = currentEntity.myUnit.getPosition().getX() - target.getPosition().getX() + currentEntity.myUnit.getPosition().getX();
		int fleeY = currentEntity.myUnit.getPosition().getY() - target.getPosition().getY() + currentEntity.myUnit.getPosition().getY();
		Position fleePosition = new Position(fleeX, fleeY);
		this.getContext().setVariable("KitePosition", fleePosition);
		
	}

	protected jbt.execution.core.ExecutionTask.Status internalTick() {
		/*
		 * TODO: this method's implementation must be completed. This function
		 * should only return Status.SUCCESS, Status.FAILURE or Status.RUNNING.
		 * No other values are allowed.
		 */
		return jbt.execution.core.ExecutionTask.Status.SUCCESS;
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