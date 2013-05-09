public class ComputationImpl
	implements Computation<MergeableResult> {

	@Inject
	private Emitter emitter;
	
	@Override
	public MergeableResult call() throws Exception {
		MergeableResult result = ...;
		Collection<Computation> other = ...;

		for (Computation c: other) {
			emitter.emit(c);
		}

		return result;
	}
}
