// ******************************************************* 
//                   MACHINE GENERATED CODE                
//                MUST BE CAREFULLY COMPLETED              
//                                                         
//           ABSTRACT METHODS MUST BE IMPLEMENTED          
//                                                         
// Generated on 04/11/2017 00:07:54
// ******************************************************* 
package bts.conditions.execution;

import bwapi.*;

/** ExecutionCondition class created from MMPM condition LowDanger. */
public class LowDanger extends
		jbt.execution.task.leaf.condition.ExecutionCondition {

	/**
	 * Constructor. Constructs an instance of LowDanger that is able to run a
	 * bts.conditions.LowDanger.
	 */
	public LowDanger(bts.conditions.LowDanger modelTask,
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
		//System.out.println(this.getClass().getCanonicalName() + " spawned");
		MoleUnit currentEntity = (MoleUnit) this.getContext().getVariable("CurrentEntity");
		List<Unit> enemies = currentEntity.getEnemiesInRadius(400);
		list<Unit> allies = currentEntity.getAlliesInRadius(400);
		int effectiveAllyStrength = currentEntity.myUnit.getHitPoints();
		int effectiveEnemyStrength = 0;
		for(Unit enemy : enemies)
		{
			if(enemy.canAttack())
			{
				effectiveEnemyStrength += enemy.getHitPoints();
			}
		}
		for(Unit ally : allies)
		{
			if(ally.canAttack())
			{
				effectiveAllyStrength += ally.getHitPoints();
			}
		}
		if(effectiveEnemyStrength > 0 && effectiveAllyStrength >= effectiveEnemyStrength)
		{
			this.getContext().setVariable("lowDanger", true);
		}
		else
		{
			this.getContext().setVariable("lowDanger", false);
		}
	}

	protected jbt.execution.core.ExecutionTask.Status internalTick() {
		/*
		 * TODO: this method's implementation must be completed. This function
		 * should only return Status.SUCCESS, Status.FAILURE or Status.RUNNING.
		 * No other values are allowed.
		 */
		this.getContext().setVariable("GameInstance", mySquad._game);
		if((boolean)this.getContext().getVariable("lowDanger") == true)
		{
			Squad mySquad = (Squad) this.getContext().getVariable("squad");
			this.getContext().setVariable("enemyTarget", mySquad.squadTarget);
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