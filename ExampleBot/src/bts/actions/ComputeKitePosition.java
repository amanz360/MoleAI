// ******************************************************* 
//                   MACHINE GENERATED CODE                
//                       DO NOT MODIFY                     
//                                                         
// Generated on 04/25/2017 16:07:41
// ******************************************************* 
package bts.actions;

/** ModelAction class created from MMPM action ComputeKitePosition. */
public class ComputeKitePosition extends jbt.model.task.leaf.action.ModelAction {

	/** Constructor. Constructs an instance of ComputeKitePosition. */
	public ComputeKitePosition(jbt.model.core.ModelTask guard) {
		super(guard);
	}

	/**
	 * Returns a bts.actions.execution .ComputeKitePosition task that is able to
	 * run this task.
	 */
	public jbt.execution.core.ExecutionTask createExecutor(
			jbt.execution.core.BTExecutor executor,
			jbt.execution.core.ExecutionTask parent) {
		return new bts.actions.execution.ComputeKitePosition(this, executor,
				parent);
	}
}