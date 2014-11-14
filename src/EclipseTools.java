import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Small class to make correct certain deficiencies eclipse has, notable, 
 * output stream synchronization. 
 * @author Wheeler
 *
 */
public class EclipseTools {

	private static List<OutputStream> streams = null;
	private static OutputStream lastStream = null;

	/**
	 * A fixed version of the output stream that the System will print to. 
	 * Adds a small delay to wait for the two streams to catch up. 
	 * @author Wheeler
	 *
	 */
	private static class FixedStream extends OutputStream {

		private final OutputStream target;

		/**
		 * Creates a stream from the original stream. 
		 * @param originalStream
		 */
		public FixedStream(OutputStream originalStream) {
			target = originalStream;
			streams.add(this);
		}

		/**
		 * Writes data to the stream.
		 * @param b The data to write. 
		 */
		@Override
		public void write(int b) throws IOException {
			if (lastStream!=this) swap();
			target.write(b);
		}

		/**
		 * Writes data to the stream.
		 * @param b The data to write. 
		 */
		@Override
		public void write(byte[] b) throws IOException {
			if (lastStream!=this) swap();
			target.write(b);
		}

		/**
		 * Writes data to the stream.
		 * @param b 	The data to write.
		 * @param off 	The offset inside the date to start writing.  
		 */
		@Override
		public void write(byte[] b, int off, int len) throws IOException {
			if (lastStream!=this) swap();
			target.write(b, off, len);
		}
		
		/**
		 * Flushes the streams and waits for them to synchronize. 
		 * @throws IOException
		 */
		private void swap() throws IOException {
			if (lastStream!=null) {
				lastStream.flush();
				try { Thread.sleep(200); } catch (InterruptedException e) {}
			}
			lastStream = this;
		}

		@Override public void close() throws IOException { target.close(); }
		@Override public void flush() throws IOException { target.flush(); }
	}

	/**
	 * Inserts a 200ms delay into the System.err or System.out OutputStreams
	 * every time the output switches from one to the other. This prevents
	 * the Eclipse console from showing the output of the two streams out of
	 * order. This function only needs to be called once.
	 */
	public static void fixConsole() {
		if (streams!=null) return;
		streams = new ArrayList<OutputStream>();
		System.setErr(new PrintStream(new FixedStream(System.err)));
		System.setOut(new PrintStream(new FixedStream(System.out)));
	}
	
	/**
	 * Tests to see if the current program is running in eclipse by looking 
	 * for the eclipse environment.
	 * @return boolean:	Whether or not the program is running in eclipse. 
	 */
	public static boolean isDevelopmentEnvironment() {
	    boolean isEclipse = true;
	    if (System.getenv("eclipse") == null) {
	        isEclipse = false;
	    }
	    return isEclipse;
	}
}