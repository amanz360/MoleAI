// ******************************************************* 
//                   MACHINE GENERATED CODE                
//                MUST BE CAREFULLY COMPLETED              
//                                                         
//           ABSTRACT METHODS MUST BE IMPLEMENTED          
//                                                         
// Generated on 04/24/2017 13:51:13
// ******************************************************* 
package bts.conditions.execution;

import java.util.List;

import bwapi.Position;
import bwapi.PositionOrUnit;
import bwapi.Unit;
import bwapi.WeaponType;
import moleAI.MoleUnit;

/** ExecutionCondition class created from MMPM condition CanAttack. */
public class CanAttack extends
		jbt.execution.task.leaf.condition.ExecutionCondition {

	/**
	 * Constructor. Constructs an instance of CanAttack that is able to run a
	 * bts.conditions.CanAttack.
	 */
	public CanAttack(bts.conditions.CanAttack modelTask,
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
		if(currentEntity.myUnit.getGroundWeaponCooldown() == 0)
		{
			this.getContext().setVariable("CanAttack", true);
			//System.out.println("I can attack!!");
		}
		else
		{
			this.getContext().setVariable("CanAttack", false);
		}
	}

	protected jbt.execution.core.ExecutionTask.Status internalTick() {
		/*
		 * TODO: this method's implementation must be completed. This function
		 * should only return Status.SUCCESS, Status.FAILURE or Status.RUNNING.
		 * No other values are allowed.
		 */
		if((boolean)this.getContext().getVariable("CanAttack") == true)
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