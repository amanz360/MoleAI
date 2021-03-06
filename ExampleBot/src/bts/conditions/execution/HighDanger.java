// ******************************************************* 
//                   MACHINE GENERATED CODE                
//                MUST BE CAREFULLY COMPLETED              
//                                                         
//           ABSTRACT METHODS MUST BE IMPLEMENTED          
//                                                         
// Generated on 04/11/2017 00:07:54
// ******************************************************* 
package bts.conditions.execution;

import java.util.List;

import bwapi.Unit;
import moleAI.MoleUnit;
import moleAI.Squad;

/** ExecutionCondition class created from MMPM condition HighDanger. */
public class HighDanger extends
		jbt.execution.task.leaf.condition.ExecutionCondition {

	/**
	 * Constructor. Constructs an instance of HighDanger that is able to run a
	 * bts.conditions.HighDanger.
	 */
	public HighDanger(bts.conditions.HighDanger modelTask,
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
		List<Unit> enemies = currentEntity.getEnemiesInRadius(250);
		List<Unit> allies = currentEntity.getAlliesInRadius(250);
		int effectiveAllyStrength = currentEntity.myUnit.getHitPoints();
		int effectiveEnemyStrength = 0;
		for(Unit enemy : enemies)
		{
			if(enemy.getType().isBuilding())
			{
				enemies.remove(enemy);
			}
		}
		/*
		for(Unit ally : allies)
		{
			if(ally.canAttack())
			{
				effectiveAllyStrength += ally.getHitPoints();
			}
		}*/
		if(enemies.size() > 0 && enemies.size() > allies.size())
		{
			this.getContext().setVariable("highDanger", false);
		}
		else
		{
			this.getContext().setVariable("highDanger", false);
		}
	}

	protected jbt.execution.core.ExecutionTask.Status internalTick() {
		/*
		 * TODO: this method's implementation must be completed. This function
		 * should only return Status.SUCCESS, Status.FAILURE or Status.RUNNING.
		 * No other values are allowed.
		 */
		if((boolean)this.getContext().getVariable("highDanger") == true)
		{
			//System.out.println("High danger situation reached");
			this.getContext().setVariable("retreatPosition", ((Squad)this.getContext().getVariable("squad")).getCenterOfSquad());
			return jbt.execution.core.ExecutionTask.Status.SUCCESS;
		}
		else
		{
			this.getContext().setVariable("rallyPosition", ((Squad)this.getContext().getVariable("squad")).rallyPosition);
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