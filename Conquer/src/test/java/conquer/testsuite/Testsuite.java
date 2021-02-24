package conquer.testsuite;

public sealed

class Testsuite permits Testsuite1, Testsuite2, Testsuite3 {
	protected int numberOfErrors;

	protected void error(final String message) {
		System.err.println("[ERROR] " + message);
		this.numberOfErrors++;
	}

	protected void expect(final Runnable runnable, final Class<? extends Throwable> expectedClass) {
		try {
			runnable.run();
		} catch (final Throwable throwable) {
			if (!expectedClass.isAssignableFrom(throwable.getClass())) {
				this.error("Expected an instanceof " + expectedClass.getCanonicalName() + " but got an instanceof "
					+ throwable.getClass().getCanonicalName() + "!");
				throwable.printStackTrace();
			} else {
				this.success("Got expected throwable: " + throwable.getClass().getCanonicalName() + ": "
					+ throwable.getMessage());
			}
			return;
		}
		this.error("Didn't get the expected throwable instanceof " + expectedClass.getCanonicalName() + "!");
		Thread.dumpStack();
	}

	protected void success(final String message) {
		System.out.println("[SUCCESS] " + message);
	}

	protected void throwable(final Throwable t) {
		System.err.println("Unexpected throwable: ");
		t.printStackTrace();
		this.numberOfErrors++;
	}
}
