// ******************************************************* 
//                   MACHINE GENERATED CODE                
//                       DO NOT MODIFY                     
//                                                         
// Generated on 04/24/2017 22:41:14
// ******************************************************* 
package bts.conditions;

/** ModelCondition class created from MMPM condition AwayFromSquad. */
public class AwayFromSquad extends jbt.model.task.leaf.condition.ModelCondition {

	/** Constructor. Constructs an instance of AwayFromSquad. */
	public AwayFromSquad(jbt.model.core.ModelTask guard) {
		super(guard);
	}

	/**
	 * Returns a bts.conditions.execution .AwayFromSquad task that is able to
	 * run this task.
	 */
	public jbt.execution.core.ExecutionTask createExecutor(
			jbt.execution.core.BTExecutor executor,
			jbt.execution.core.ExecutionTask parent) {
		return new bts.conditions.execution.AwayFromSquad(this, executor,
				parent);
	}
}