// ******************************************************* 
//                   MACHINE GENERATED CODE                
//                       DO NOT MODIFY                     
//                                                         
// Generated on 04/19/2017 12:51:27
// ******************************************************* 
package btlibrary;

/**
 * BT library that includes the trees read from the following files:
 * <ul>
 * <li>C:\Users\amanz\OneDrive\Documents\jbt-master\JBTCore\TerranMarine.xbt</li>
 * </ul>
 */
public class TerranMarineBTLibrary implements jbt.execution.core.IBTLibrary {
	/**
	 * Tree generated from file
	 * C:\Users\amanz\OneDrive\Documents\jbt-master\JBTCore\TerranMarine.xbt.
	 */
	private static jbt.model.core.ModelTask TerranMarine;

	/* Static initialization of all the trees. */
	static {
		TerranMarine = new jbt.model.task.decorator.ModelRepeat(
				null,
				new jbt.model.task.composite.ModelDynamicPriorityList(
						null,
						new bts.actions.Attack(new bts.conditions.LowDanger(
								null), null, "enemyTarget"),
						new jbt.model.task.composite.ModelSequence(
								new bts.conditions.HighDanger(null),
								new bts.actions.ComputeClosestBasePosition(null),
								new bts.actions.Move(null, null,
										"retreatPosition")),
						new bts.actions.AttackMove(null, null, "rallyPosition")));

	}

	/**
	 * Returns a behaviour tree by its name, or null in case it cannot be found.
	 * It must be noted that the trees that are retrieved belong to the class,
	 * not to the instance (that is, the trees are static members of the class),
	 * so they are shared among all the instances of this class.
	 */
	public jbt.model.core.ModelTask getBT(java.lang.String name) {
		if (name.equals("TerranMarine")) {
			return TerranMarine;
		}
		return null;
	}

	/**
	 * Returns an Iterator that is able to iterate through all the elements in
	 * the library. It must be noted that the iterator does not support the
	 * "remove()" operation. It must be noted that the trees that are retrieved
	 * belong to the class, not to the instance (that is, the trees are static
	 * members of the class), so they are shared among all the instances of this
	 * class.
	 */
	public java.util.Iterator<jbt.util.Pair<java.lang.String, jbt.model.core.ModelTask>> iterator() {
		return new BTLibraryIterator();
	}

	private class BTLibraryIterator
			implements
			java.util.Iterator<jbt.util.Pair<java.lang.String, jbt.model.core.ModelTask>> {
		static final long numTrees = 1;
		long currentTree = 0;

		public boolean hasNext() {
			return this.currentTree < numTrees;
		}

		public jbt.util.Pair<java.lang.String, jbt.model.core.ModelTask> next() {
			this.currentTree++;

			if ((this.currentTree - 1) == 0) {
				return new jbt.util.Pair<java.lang.String, jbt.model.core.ModelTask>(
						"TerranMarine", TerranMarine);
			}

			throw new java.util.NoSuchElementException();
		}

		public void remove() {
			throw new java.lang.UnsupportedOperationException();
		}
	}
}
