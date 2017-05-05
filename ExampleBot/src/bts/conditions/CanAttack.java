// ******************************************************* 
//                   MACHINE GENERATED CODE                
//                       DO NOT MODIFY                     
//                                                         
// Generated on 04/24/2017 13:51:13
// ******************************************************* 
package bts.conditions;

/** ModelCondition class created from MMPM condition CanAttack. */
public class CanAttack extends jbt.model.task.leaf.condition.ModelCondition {

	/** Constructor. Constructs an instance of CanAttack. */
	public CanAttack(jbt.model.core.ModelTask guard) {
		super(guard);
	}

	/**
	 * Returns a bts.conditions.execution .CanAttack task that is able to run
	 * this task.
	 */
	public jbt.execution.core.ExecutionTask createExecutor(
			jbt.execution.core.BTExecutor executor,
			jbt.execution.core.ExecutionTask parent) {
		return new bts.conditions.execution.CanAttack(this, executor, parent);
	}
}