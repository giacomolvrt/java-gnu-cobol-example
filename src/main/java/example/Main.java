package example;

import com.sun.jna.Library;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

/*
 * libcob interface, initialising GnuCOBOL run time
 */
interface libcob extends Library {
	libcob INSTANCE = (libcob) Native.loadLibrary("cob", libcob.class);

	void cob_init(int argc, Pointer argv);
}

/*
 * first COBOL program interface, single program
 */
interface subtest extends Library {
	subtest INSTANCE = (subtest) Native.loadLibrary("cobsubtest", subtest.class);

	int cobsubtest(Pointer aValue);
}

/*
 * second COBOL program interface, single program
 */
interface subtest2 extends Library {
	subtest2 INSTANCE = (subtest2) Native.loadLibrary("cobsubtest2", subtest2.class);

	int cobsubtest2(Pointer aValue);
}

public class Main {

	public static void main(String arg[]) {

		System.out.println("Ecco un programma di esempio per richiamare il cobol su unix");

		/*
		 * try and initialise the GnuCOBOL run time calling cob_init with no parameters
		 */
		System.setProperty("jna.library.path", "/usr/lib/x86_64-linux-gnu/");

		/*
		 * oppure java -Djna.library.path=<path to the library> Main
		 */
		try {
			libcob.INSTANCE.cob_init(0, null);
		} catch (UnsatisfiedLinkError e) {
			System.out.println("Libcob Exception" + e);
		}

		/*
		 * call a GnuCOBOL program, passing a PIC X(72) space filled
		 */
		System.setProperty("jna.library.path", "/home/giacomo/progetti/cobol/modules/");

		try {
			// JAVA string
			String stringThing = new String("We Did It!");

			// make a Pointer and space fill
			Pointer pointer;
			pointer = new Memory(72);
			byte space = 32;
			pointer.setMemory(0, 72, space);
			byte[] data = Native.toByteArray(stringThing);
			pointer.write(0, data, 0, data.length - 1);

			int rc;

			// call the GnuCOBOL program
			rc = subtest.INSTANCE.cobsubtest(pointer);

			// display return-code
			System.out.print("COBOL Return Code ");
			System.out.println(rc);

			// call the second test
			rc = subtest2.INSTANCE.cobsubtest2(pointer);
			System.out.print("COBOL Return Code ");
			System.out.println(rc);

		} catch (UnsatisfiedLinkError e) {
			System.out.println("subtest Exception" + e);
		}
	}

}
