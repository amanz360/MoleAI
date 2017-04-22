// ******************************************************* 
//                   MACHINE GENERATED CODE                
//                MUST BE CAREFULLY COMPLETED              
//                                                         
//           ABSTRACT METHODS MUST BE IMPLEMENTED          
//                                                         
// Generated on 04/11/2017 00:07:54
// ******************************************************* 
package bts.actions.execution;
import bwapi.*;
import moleAI.Information;
import moleAI.MoleUnit;

/** ExecutionAction class created from MMPM action Attack. */
public class Attack extends jbt.execution.task.leaf.action.ExecutionAction {
	/**
	 * Value of the parameter "target" in case its value is specified at
	 * construction time. null otherwise.
	 */
	private Unit target;
	/**
	 * Location, in the context, of the parameter "target" in case its value is
	 * not specified at construction time. null otherwise.
	 */
	private java.lang.String targetLoc;

	/**
	 * Constructor. Constructs an instance of Attack that is able to run a
	 * bts.actions.Attack.
	 * 
	 * @param target
	 *            value of the parameter "target", or null in case it should be
	 *            read from the context. If null,
	 *            <code>targetLoc<code> cannot be null.
	 * @param targetLoc
	 *            in case <code>target</code> is null, this variable represents
	 *            the place in the context where the parameter's value will be
	 *            retrieved from.
	 */
	public Attack(bts.actions.Attack modelTask,
			jbt.execution.core.BTExecutor executor,
			jbt.execution.core.ExecutionTask parent, Unit target,
			java.lang.String targetLoc) {
		super(modelTask, executor, parent);

		this.target = target;
		this.targetLoc = targetLoc;
	}

	/**
	 * Returns the value of the parameter "target", or null in case it has not
	 * been specified or it cannot be found in the context.
	 */
	public Unit getTarget() {
		if (this.target != null) {
			return this.target;
		} else {
			return (Unit) this.getContext().getVariable(
					this.targetLoc);
		}
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
		Game game = (Game) this.getContext().getVariable("GameInstance");
		Unit myTarget = (Unit) this.getTarget();
		currentEntity.smartAttackUnit(myTarget, game);
		currentEntity.setJob(Information.Job.ATTACK);
	}

	protected jbt.execution.core.ExecutionTask.Status internalTick() {
		/*
		 * TODO: this method's implementation must be completed. This function
		 * should only return Status.SUCCESS, Status.FAILURE or Status.RUNNING.
		 * No other values are allowed.
		 */
		MoleUnit currentEntity = (MoleUnit) this.getContext().getVariable("CurrentEntity");
		Game game = (Game) this.getContext().getVariable("GameInstance");
		Unit myTarget = (Unit) this.getTarget();
		if(!currentEntity.myUnit.exists() || currentEntity.myUnit.getHitPoints() <= 0)
		{
			return jbt.execution.core.ExecutionTask.Status.FAILURE;
		}
		else if(myTarget.getHitPoints() <= 0 || !myTarget.exists())
		{
			return jbt.execution.core.ExecutionTask.Status.SUCCESS;
		}
		else
		{
			if(currentEntity.myUnit.isIdle())
			{
				currentEntity.smartAttackUnit(myTarget, game);
				currentEntity.setJob(Information.Job.ATTACK);
			}
			return jbt.execution.core.ExecutionTask.Status.RUNNING;
		}
	}

	protected void internalTerminate() {
		/* TODO: this method's implementation must be completed. */
		MoleUnit currentEntity = (MoleUnit) this.getContext().getVariable("CurrentEntity");
		currentEntity.myUnit.stop();
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