/****************************************************************************
 * NCSA HDF                                                                 *
 * National Comptational Science Alliance                                   *
 * University of Illinois at Urbana-Champaign                               *
 * 605 E. Springfield, Champaign IL 61820                                   *
 *                                                                          *
 * For conditions of distribution and use, see the accompanying             *
 * hdf/COPYING file.                                                        *
 *                                                                          *
 ****************************************************************************/

package ncsa.hdf.hdf5lib;

import java.io.*;
import java.net.*;
import java.lang.*;
import java.util.*;
import ncsa.hdf.hdf5lib.exceptions.*;


/**
 *  <hr>
 *  <p>
 *  <center>
 *  <b>This class is the Java interface for the HDF5 library</b>
 *  </center>
 *  <p>
 *  This code is the called by Java programs to access the
 *  entry points of the HDF5 1.2 library.
 *  Each routine wraps a single HDF5 entry point, generally with the
 *  arguments and return codes analogous to the C interface.
 *  <p>
 *  For details of the HDF5 library, see the HDF5 Documentation at:
 *  <a href="http://hdf.ncsa.uiuc.edu/HDF5/">http://hdf.ncsa.uiuc.edu/HDF5/</a>
 *  <hr>
 *  <p>
 *  <b>Mapping of arguments for Java</b>
 *
 *  <p>
 *  In general, arguments to the HDF Java API are straightforward
 *  translations from the 'C' API described in the HDF Reference
 *  Manual.
 *  <p>
 *
 *  <center>
 *  <table border=2 cellpadding=2>
 *  <caption><b>HDF-5 C types to Java types</b>   </caption>
 *  <tr><td> <b>HDF-5</b>       </td><td> <b>Java</b>    </td></tr>
 *  <tr><td> H5T_NATIVE_INT     </td><td> int, Integer   </td></tr>
 *  <tr><td> H5T_NATIVE_SHORT   </td><td> short, Short   </td></tr>
 *  <tr><td> H5T_NATIVE_FLOAT   </td><td> float, Float   </td></tr>
 *  <tr><td> H5T_NATIVE_DOUBLE  </td><td> double, Double </td></tr>
 *  <tr><td> H5T_NATIVE_CHAR    </td><td> byte, Byte     </td></tr>
 *  <tr><td> H5T_C_S1           </td><td> java.lang.String    </td></tr>
 *  <tr><td> void * <BR>(i.e., pointer to `Any')     </td><td> Special -- see HDFArray </td></tr>
 *  </table>
 *  </center>
 *  <p>
 *  <center>
 *  <b>General Rules for Passing Arguments and Results</b>
 *  </center>
 *  <p>
 *  In general, arguments passed <b>IN</b> to Java are the analogous basic types, as above.
 *  The exception is for arrays, which are discussed below.
 *  <p>
 *  The <i>return value</i> of Java methods is also the analogous type, as above.
 *  A major exception to that rule is that all HDF functions that
 *  return SUCCEED/FAIL are declared <i>boolean</i> in the Java version, rather than
 *  <i>int</i> as in the C.
 *  Functions that return a value or else FAIL are declared
 *  the equivalent to the C function.
 *  However, in most cases the Java method will raise an exception instead
 *  of returning an error code.  See 
 *  <a href="#ERRORS">Errors and Exceptions</a> below.
 *  <p>
 *  Java does not support pass by reference of arguments, so
 *  arguments that are returned through <b>OUT</b> parameters
 *  must be wrapped in an object or array.
 *  The Java API for HDF consistently wraps arguments in
 *  arrays.
 *  <p>
 *  For instance, a function that returns two integers is
 *  declared:
 *  <p>
 *  <pre>
 *       h_err_t HDF5dummy( int *a1, int *a2)
 *  </pre>
 *  For the Java interface, this would be declared:
 *  <p>
 *  <pre>
 *       public static native int HDF5dummy( int args[] );
 *  </pre>
 *  where <i>a1</i> is <i>args[0]</i>
 *  and <i>a2</i> is <i>args[1]</i>, and would be invoked:
 *  <p>
 *  <pre>
 *       H5.HDF5dummy( a );
 *  </pre>
 *  <p>
 *  All the routines where this convention is used will have
 *  specific documentation of the details, given below.
 *  <p>
 *  <a NAME="ARRAYS">
 *  <b>Arrays</b>
 *  </a>
 *  <p>
 *  HDF5 needs to read and write multi-dimensional arrays
 *  of any number type (and records).
 *  The HDF5 API describes the layout of the source and destination, 
 *  and the data for the array passed as a block of bytes, for instance,
 *  <p>
 *  <pre>
 *      herr_t H5Dread(int fid, int filetype, int memtype, int memspace, 
 *      void * data);
 *  </pre>
 *  <p>
 *  where ``void *'' means that the data may be any valid numeric
 *  type, and is a contiguous block of bytes that is the data
 *  for a multi-dimensional array.  The other parameters describe the
 *  dimensions, rank, and datatype of the array on disk (source) and
 *  in memory (destination).  
 *  <p>
 *  For Java, this ``ANY'' is a problem, as the type of data must
 *  always be declared.  Furthermore, multidimensional arrays
 *  are definitely <i>not</i> layed out contiguously
 *  in memory.
 *  It would be infeasible to declare a separate routine for
 *  every combination of number type and dimensionality.
 *  For that reason, the 
 *  <a href="./ncsa.hdf.hdf5lib.HDFArray.html><b>HDFArray</b></a> 
 *  class is used to
 *  discover the type, shape, and size of the data array
 *  at run time, and to convert to and from a contiguous array
 *  of bytes in static native C order.
 *  <p>
 *  The upshot is that any Java array of numbers (either primitive
 *  or sub-classes of type <b>Number</b>) can be passed as 
 *  an ``Object'', and the Java API will translate to and from 
 *  the appropriate packed array of bytes needed by the C library.
 *  So the function above would be declared:
 *  <p>
 *  <pre>
 *      public static native int H5Dread(int fid, int filetype, 
 *          int memtype, int memspace, Object data);
 *  </pre>
 *  and the parameter <i>data</i> can be any multi-dimensional
 *  array of numbers, such as float[][], or int[][][], or Double[][].
 *  <p>
 *  <a NAME="CONSTANTS">
 *  <b>HDF-5 Constants</b>
 *  <p>
 *  The HDF-5 API defines a set of constants and enumerated values.
 *  Most of these values are available to Java programs via the class 
 *  <a href="./ncsa.hdf.hdf5lib.HDF5Constants.html">
 *  <b>HDF5Constants</b></a>.
 *  For example, the parameters for the h5open() call include two
 *  numeric values, <b><i>HDFConstants.H5F_ACC_RDWR</i></b> and 
 *  <b><i>HDF5Constants.H5P_DEFAULT</i></b>.  As would be expected, 
 *  these numbers correspond to the C constants <b><i>H5F_ACC_RDWR</i></b>
 *  and <b><i>H5P_DEFAULT</i></b>.
 *  <p>
 *  The HDF-5 API defines a set of values that describe number types and
 *  sizes, such as "H5T_NATIVE_INT" and "hsize_t". These values are
 *  determined at run time by the HDF-5 C library. 
 *  To support these parameters,
 *  the Java class 
 *  <a href="./ncsa.hdf.hdf5lib.HDF5CDataTypes.html">
 *  <b>HDF5CDataTypes</b></a> looks up the values when 
 *  initiated.  The values can be accessed as public variables of the 
 *  Java class, such as:
 *  <pre> int data_type = HDF5CDataTypes.JH5T_NATIVE_INT;</pre>
 *  The Java application uses both types of constants the same way, the only
 *  difference is that the <b><i>HDF5CDataTypes</i></b> may have different
 *  values on different platforms.
 *  <p>
 *  <a NAME="ERRORS">
 *  <b>Error handling and Exceptions</b>
 *  <p>
 *  The HDF5 error API (H5E) manages the behavior of the error stack in
 *  the HDF-5 library. This API is omitted from the JHI5. Errors
 *  are converted into Java exceptions. This is totally different from the
 *  C interface, but is very natural for Java programming.
 *  <p>
 *  The exceptions of the JHI5 are organized as sub-classes of the class
 *  <a href="./ncsa.hdf.hdf5lib.exceptions.HDF5Exception.html">
 *  <b>HDF5Exception</b></a>.  There are two subclasses of 
 *  <b>HDF5Exception</b>, 
 *  <a href="./ncsa.hdf.hdf5lib.exceptions.HDF5LibraryException.html">
 *  <b>HDF5LibraryException</b></a>
 *  and 
 *  <a href="./ncsa.hdf.hdf5lib.exceptions.HDF5JavaException.html">
 *  <b>HDF5JavaException</b></a>. The sub-classes of the former
 *  represent errors from the HDF-5 C library, while sub-classes of the latter
 *  represent errors in the JHI5 wrapper and support code.
 *  <p>
 *  The super-class <b><i>HDF5LibraryException</i></b> implements the method
 *  '<b><i>printStackTrace()</i></b>', which prints out the HDF-5 error stack,
 *  as described in the HDF-5 C API <i><b>H5Eprint()</b>.</i> This may
 *  be used by Java exception handlers to print out the HDF-5 error stack.
 *  <hr>
 *
 *  @version HDF5 1.2 <BR>
 *  <b>See also:
 *  <a href ="./ncsa.hdf.hdf5lib.HDFArray.html">
 *  </b> ncsa.hdf.hdf5lib.HDFArray</a><BR>
 *  <a href ="./ncsa.hdf.hdf5lib.HDF5Constants.html">
 *  </b> ncsa.hdf.hdf5lib.HDF5Constants</a><BR>
 *  <a href ="./ncsa.hdf.hdf5lib.HDF5CDataTypes.html">
 *  </b> ncsa.hdf.hdf5lib.HDF5CDataTypes</a><BR>
 *  <a href ="./ncsa.hdf.hdf5lib.HDF5Exception.html">
 *  ncsa.hdf.hdf5lib.HDF5Exception<BR>
 *  <a href="http://hdf.ncsa.uiuc.edu/HDF5/">
 *  http://hdf.ncsa.uiuc.edu/HDF5"</a>
**/
public class H5 {
	public final static String H5PATH_PROPERTY_KEY = "ncsa.hdf.hdf5lib.H5.hdf5lib";

	static 
	{
		String filename = null;
		filename = System.getProperty(H5PATH_PROPERTY_KEY,null);
		if ((filename != null) && (filename.length() > 0))
		{
			File h5dll = new File(filename);
			if (h5dll.exists() && h5dll.canRead() && h5dll.isFile()) {
				System.load(filename);
			} else {
				throw (new UnsatisfiedLinkError("Invalid HDF5 library, "+filename));
			}
		}
		else {
			System.loadLibrary("jhdf5");
		}

		/* Important!  Exit quietly */
		try {
		H5.H5dont_atexit();
		} catch (HDF5LibraryException e) {
			System.exit(1);
		}

		/* Important!  Disable error output to C stdout */
		H5.H5error_off();  

		/*  Optional:  confirm the version 
                 *     This will crash immediately if not the
                 *     specified version.
		 */
		Integer majnum = Integer.getInteger("ncsa.hdf.hdf5lib.H5.hdf5maj",null);
		Integer minnum = Integer.getInteger("ncsa.hdf.hdf5lib.H5.hdf5min",null);
		Integer relnum = Integer.getInteger("ncsa.hdf.hdf5lib.H5.hdf5rel",null);
		if ((majnum != null) && (minnum != null) && (relnum != null)) {
			H5.H5check_version(majnum.intValue(),minnum.intValue(),relnum.intValue());
		}

	}

       //////////////////////////////////////////////////////////////////

	/**
	 *  J2C converts a Java constant to an HDF5 constant determined at runtime
	 *
	 *  @param java_constant The value of Java constant
	 *  @return the value of an HDF5 constant determined at runtime
	**/
	public static native int J2C(int java_constant);

	/** Turn off error handling 
          * By default, the C library prints the error stack
          * of the HDF-5 C library on stdout.  This behavior
          * may be disabled by calling H5error_off().
          */
	private static native int H5error_off();


	//////////////////////////////////////////////////////////////
	//                                                          //
	//             H5: General Library Functions                //
	//                                                          //
	//////////////////////////////////////////////////////////////


	/**
	 *  H5open initialize the library.
	 *
	 *  @return a non-negative value if successful 
	 *
	 *  @exception HDF5LibraryException 
	 *  - Error from the HDF-5 Library.
	 **/
	public static native int H5open() throws HDF5LibraryException;


	/**
	 *  H5close flushes all data to disk, closes all file 
	 *  identifiers, and cleans up all memory used by the library.
	 *
	 *  @return a non-negative value if successful
	 *
	 *  @exception HDF5LibraryException 
	 *  - Error from the HDF-5 Library.
	 **/
	public static native int H5close() throws HDF5LibraryException;


	/**
	 *  H5dont_atexit indicates to the library that an atexit() 
         *  cleanup routine should not be installed.  In order to be 
         *  effective, this routine must be called before any other HDF
	 *  function calls, and must be called each time the library 
         *  is loaded/linked into the application (the first time and 
         *  after it's been un-loaded).
	 *  <P>
	 *  This is called by the static initializer, so this should
         *  never need to be explicitly called by a Java program.
	 *
	 *  @return a non-negative value if successful
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	private static native int H5dont_atexit() throws HDF5LibraryException;

	/**
	 *  H5get_libversion retrieves the major, minor, and release 
         *  numbers of the version of the HDF library which is linked 
         *  to the application.
	 *
	 *  @param libversion The version information of the HDF library.
	 *    <pre>
	 *      libversion[0] = The major version of the library.
	 *      libversion[1] = The minor version of the library.
	 *      libversion[2] = The release number of the library.
	 *    </pre>
	 *  @return a non-negative value if successful, along with
	 *  the version information. 
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native int H5get_libversion(int[] libversion) throws HDF5LibraryException;


	/**
	 *  H5check_version verifies that the arguments match the version numbers
	 *  compiled into the library.
	 *
	 *  @param majnum The major version of the library.
	 *  @param minnum The minor version of the library.
	 *  @param relnum The release number of the library.
	 *  @param patnum The patch number of the library.
	 *  @return a non-negative value if successful. 
         *  Upon failure (when the versions do not match), this function
	 *  causes the application to abort (i.e., crash)
	 *
	 *  @see C API function: herr_t H5check_version()
	 **/
	public static native int H5check_version(int majnum, int minnum, int relnum);

	//////////////////////////////////////////////////////////////
	//                                                          //
	//             H5E: Error Stack                             //
	//                                                          //
	//////////////////////////////////////////////////////////////

	/**
	 *   H5Eclear clears the error stack for the current thread.
	 *   H5Eclear can fail if there are problems initializing the 
	 *   library.
	 *   <p>
	 *   This may be used by exception handlers to assure that
	 *   the error condition in the HDF-5 library has been reset.
	 * 
	 *   @return Returns a non-negative value if successful
	 * 
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native int H5Eclear() throws HDF5LibraryException;


	//////////////////////////////////////////////////////////////
	//                                                          //
	//             H5A: Attribute Interface Functions           //
	//                                                          //
	//////////////////////////////////////////////////////////////

	/**
	 *  H5Acreate creates an attribute which is attached to the 
         *  object specified with loc_id.
	 *
	 *  @param loc_id IN: Object (dataset, group, or named datatype) to be attached to.
	 *  @param name IN: Name of attribute to create.
	 *  @param type_id IN: Identifier of datatype for attribute.
	 *  @param space_id IN: Identifier of dataspace for attribute.
	 *  @param create_plist IN: Identifier of creation property 
	 *  list (currently not used).
	 *
	 *  @return an attribute identifier if successful
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 *  @exception NullPointerException - name is null.
	 **/
	public static native int H5Acreate(int loc_id, String name, 
		int type_id, int space_id, int create_plist)
		throws HDF5LibraryException, NullPointerException;

	/**
	 *  H5Aopen_name opens an attribute specified by its name, 
	 *  name, which is attached to the object specified with loc_id.
	 *
	 *  @param loc_id  IN: Identifier of a group, dataset, or named datatype atttribute
	 *  @param name IN: Attribute name.
	 *
	 *  @return attribute identifier if successful
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 *  @exception NullPointerException - name is null.
	 **/
	public static native int H5Aopen_name(int loc_id, String name)
		throws HDF5LibraryException, NullPointerException;


	/**
	 *  H5Aopen_idx opens an attribute which is attached to the 
	 *  object specified with loc_id.  The location object may 
	 *  be either a group, dataset, or named datatype, all of 
	 *  which may have any sort of attribute.
	 *
	 *  @param loc_id IN: Identifier of the group, dataset, or 
	 *  named datatype attribute
	 *  @param idx IN: Index of the attribute to open.
	 *
	 *  @return attribute identifier if successful
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native int H5Aopen_idx(int loc_id, int idx)
		throws HDF5LibraryException;


	/**
	 *  H5Awrite writes an attribute, specified with attr_id. The 
	 *  attribute's memory datatype is specified with mem_type_id. 
	 *  The entire attribute is written from buf to the file.
	 *
	 *  @param attr_id  IN: Identifier of an attribute to write.
	 *  @param mem_type_id IN: Identifier of the attribute datatype 
	 *  (in memory).
	 *  @param buf IN: Data to be written.
	 *
	 *  @return a non-negative value if successful
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 *  @exception NullPointerException - data is null.
	 **/
	public static native int H5Awrite(int attr_id, int mem_type_id, 
		byte[] buf)
		throws HDF5LibraryException, NullPointerException;

	/**
	 *  H5Awrite writes an attribute, specified with attr_id. The 
	 *  attribute's memory datatype is specified with mem_type_id. 
	 *  The entire attribute is written from data object to the file.
	 *
	 *  @param attr_id  IN: Identifier of an attribute to write.
	 *  @param mem_type_id IN: Identifier of the attribute datatype 
	 *  (in memory).
	 *  @param obj IN: Data object to be written.
	 *
	 *  @return a non-negative value if successful
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 *  @exception NullPointerException - data object is null.
	 *  @see public static native int H5Awrite(int attr_id, int mem_type_id, byte[] buf);
	 **/
	public static int H5Awrite(int attr_id, int mem_type_id,
	 Object obj )
	 throws HDF5Exception, NullPointerException
	{
		HDFArray theArray = new HDFArray(obj);
		byte[] buf = theArray.byteify();

		int retVal = H5Awrite(attr_id, mem_type_id, buf);
		buf = null;
		theArray = null;
		return retVal;
	}


	/**
	 *  H5Aread reads an attribute, specified with attr_id. The 
	 *  attribute's memory datatype is specified with mem_type_id. 
	 *  The entire attribute is read into buf from the file.
	 *
	 *  @param attr_id IN: Identifier of an attribute to read.
	 *  @param mem_type_id IN: Identifier of the attribute datatype 
	 *  (in memory).
	 *  @param buf IN: Buffer for data to be read.
	 *
	 *  @return a non-negative value if successful
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 *  @exception NullPointerException - data buffer is null.
	 **/
	public static native int H5Aread(int attr_id, int mem_type_id, byte[] buf)
		throws HDF5LibraryException, NullPointerException;

	/**
	 *  H5Aread reads an attribute, specified with attr_id. The 
	 *  attribute's memory datatype is specified with mem_type_id. 
	 *  The entire attribute is read into data object from the file.
	 *
	 *  @param attr_id IN: Identifier of an attribute to read.
	 *  @param mem_type_id IN: Identifier of the attribute datatype 
	 *  (in memory).
	 *  @param obj IN: Object for data to be read.
	 *
	 *  @return a non-negative value if successful
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 *  @exception NullPointerException - data buffer is null.
	 *  @see public static native int H5Aread( )
	**/
	public static int H5Aread(int attr_id, int mem_type_id, Object obj)
	 throws HDF5Exception, NullPointerException
	{
		HDFArray theArray = new HDFArray(obj);
		byte[] buf = theArray.emptyBytes();

		//  This will raise an exception if there is an error
		int status = H5Aread(attr_id, mem_type_id, buf);

		// No exception:  status really ought to be OK
 		if (status >= 0) {
			obj = theArray.arrayify( buf);
		}

		return status;
	}

	/**
	 *  H5Aget_space retrieves a copy of the dataspace for an 
         *  attribute.
	 *
	 *  @param attr_id IN: Identifier of an attribute.
	 *
	 *  @return attribute dataspace identifier if successful
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native int H5Aget_space(int attr_id) throws HDF5LibraryException;


	/**
	 *  H5Aget_type retrieves a copy of the datatype for an attribute.
	 *
	 *  @param attr_id  IN: Identifier of an attribute.
	 *
	 *  @return a datatype identifier if successful
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native int H5Aget_type(int attr_id) throws HDF5LibraryException;


	/**
	 *  H5Aget_name retrieves the name of an attribute specified by 
	 *  the identifier, attr_id.
	 *
	 *  @param attr_id  IN: Identifier of the attribute.
	 *  @param buf_size  IN: The size of the buffer to store the 
 	 *  name in.
	 *  @param name OUT: Buffer to store name in.
	 *
	 *  @exception ArrayIndexOutOfBoundsException  JNI error writing 
	 *  back array
	 *  @exception ArrayStoreException   JNI error writing back array
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 *  @exception NullPointerException - name is null.
	 *  @exception IllegalArgumentException - bub_size <= 0.
	 *
	 *  @return the length of the attribute's name if successful.
	 **/
	public static native long H5Aget_name(int attr_id, long buf_size, 
		String[] name)
		throws ArrayIndexOutOfBoundsException, 
		ArrayStoreException, 
		HDF5LibraryException, 
		NullPointerException, 
		IllegalArgumentException;

	/**
	 *  H5Aget_num_attrs returns the number of attributes attached 
	 *  to the object specified by its identifier, loc_id.
	 *
	 *  @param loc_id  IN: Identifier of a group, dataset, or named 
	 *  datatype.
	 *
	 *  @return the number of attributes if successful
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native int H5Aget_num_attrs(int loc_id)
		throws HDF5LibraryException;

	/**
	 *  H5Adelete removes the attribute specified by its name, name, 
	 *  from a dataset, group, or named datatype.
	 *
	 *  @param loc_id  IN: Identifier of the dataset, group, or 
	 *  named datatype.
	 *  @param name  IN: Name of the attribute to delete.
	 *
	 *  @return a non-negative value if successful
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 *  @exception NullPointerException - name is null.
	 **/
	public static native int H5Adelete(int loc_id, String name)
		throws HDF5LibraryException, NullPointerException;


	/**
	 *  H5Aclose terminates access to the attribute specified by 
	 *  its identifier, attr_id.
	 *
	 *  @param attr_id  IN: Attribute to release access to.
	 *
	 *  @return a non-negative value if successful
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native int H5Aclose(int attr_id)
		throws HDF5LibraryException;


	//////////////////////////////////////////////////////////////
	//                                                          //
	//             H5D: Datasets Interface Functions            //
	//                                                          //
	//////////////////////////////////////////////////////////////

	/**
	 *  H5Dcreate creates a data set with a name, name, in the file 
	 *  or in the  group specified by the identifier loc_id.
	 *
	 *  @param loc_id Identifier of the file or group to create the 
	 *  dataset within.
	 *  @param name The name of the dataset to create.
	 *  @param type_id Identifier of the datatype to use when 
	 *  creating the dataset.
	 *  @param space_id Identifier of the dataspace to use when 
	 *  creating the dataset.
	 *  @param create_plist_id Identifier of the set creation 
	 *  property list.
	 *
	 *  @return a dataset identifier if successful
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 *  @exception NullPointerException - name is null.
	 **/
	public static native int H5Dcreate(int loc_id, String name, 
		int type_id, int space_id, int create_plist_id)
		throws HDF5LibraryException, NullPointerException;


	/**
	 *  H5Dopen opens an existing dataset for access in the file or 
	 *  group specified in loc_id.
	 *
	 *  @param loc_id  Identifier of the dataset to open or the file 
	 *  or group
	 *  @param name  The name of the dataset to access.
	 *
	 *  @return a dataset identifier if successful
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 *  @exception NullPointerException - name is null.
	**/
	public static native int H5Dopen(int loc_id, String name)
		throws HDF5LibraryException, NullPointerException;


	/**
	 *  H5Dget_space returns an identifier for a copy of the 
	 *  dataspace for a dataset.
	 *
	 *  @param dataset_id Identifier of the dataset to query.
	 *
	 *  @return a dataspace identifier if successful
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native int H5Dget_space(int dataset_id) 
		throws HDF5LibraryException ;


	/**
	 *  H5Dget_type returns an identifier for a copy of the datatype 
	 *  for a dataset.
	 *
	 *  @param dataset_id Identifier of the dataset to query.
	 *
	 *  @return a datatype identifier if successful
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native int H5Dget_type(int dataset_id) 
		throws HDF5LibraryException;


	/**
	 *  H5Dget_create_plist returns an identifier for a copy of the 
	 *  dataset creation property list for a dataset.
	 *
	 *  @param dataset_id Identifier of the dataset to query.
	 *  @return a dataset creation property list identifier
	 *  if successful
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native int H5Dget_create_plist(int dataset_id) 
		throws HDF5LibraryException;


	/**
	 *  H5Dread reads a (partial) dataset, specified by its 
	 *  identifier dataset_id, from the file into the application 
	 *  memory buffer buf.
	 *
	 *  @param dataset_id  Identifier of the dataset read from.
	 *  @param mem_type_id  Identifier of the memory datatype.
	 *  @param mem_space_id  Identifier of the memory dataspace.
	 *  @param file_space_id  Identifier of the dataset's dataspace 
	 *  in the file.
	 *  @param xfer_plist_id  Identifier of a transfer property 
	 *  list for this I/O operation.
	 *  @param buf Buffer to store data read from the file.
	 * 
	 *  @return a non-negative value if successful
	 * 
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 *  @exception NullPointerException - data buffer is null.
	 **/
	public static native int H5Dread(int dataset_id, int mem_type_id, 
		int mem_space_id, int file_space_id, int xfer_plist_id, 
		byte[] buf)
		throws HDF5LibraryException, NullPointerException;

	/**
	 *  H5Dread reads a (partial) dataset, specified by its 
	 *  identifier dataset_id, from the file into the application 
	 *  data object.
	 *
	 *  @param dataset_id  Identifier of the dataset read from.
	 *  @param mem_type_id  Identifier of the memory datatype.
	 *  @param mem_space_id  Identifier of the memory dataspace.
	 *  @param file_space_id  Identifier of the dataset's dataspace 
	 *  in the file.
	 *  @param xfer_plist_id  Identifier of a transfer property 
	 *  list for this I/O operation.
	 *  @param obj Object to store data read from the file.
	 *
	 *  @return a non-negative value if successful
	 *
	 *  @exception HDF5Exception - Failure in the data conversion.
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 *  @exception NullPointerException - data object is null.
	 **/
	public static int H5Dread(int dataset_id, int mem_type_id, 
		int mem_space_id, int file_space_id, 
		int xfer_plist_id, Object obj )
		throws HDF5Exception, 
		HDF5LibraryException, 
		NullPointerException
	{
		/*  Create a data buffer to hold
			the data into a Java Array */
		HDFArray theArray = new HDFArray(obj);
		byte[] buf = theArray.emptyBytes();

		/*  will raise exception if read fails */
		int status = H5Dread(dataset_id, mem_type_id, mem_space_id,
			file_space_id, xfer_plist_id, buf);
		if (status >= 0) {
			/*  convert the data into a Java Array */
			obj = theArray.arrayify( buf);
		}
		/* clean up these:  assign 'null' as hint to gc() */
		buf = null;
		theArray = null;
		return status;
	}


	/**
	 *  H5Dwrite writes a (partial) dataset, specified by its 
	 *  identifier dataset_id, from the application memory buffer 
	 *  buf into the file.
	 *
	 *  @param dataset_id  Identifier of the dataset read from.
	 *  @param mem_type_id  Identifier of the memory datatype.
	 *  @param mem_space_id  Identifier of the memory dataspace.
	 *  @param file_space_id  Identifier of the dataset's dataspace 
	 *  in the file.
	 *  @param xfer_plist_id  Identifier of a transfer property 
	 *  list for this I/O operation.
	 *  @param buf Buffer with data to be written to the file.
	 *
	 *  @return a non-negative value if successful
	 * 
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 *  @exception NullPointerException - name is null.
	 **/
	public static native int H5Dwrite(int dataset_id, 
		int mem_type_id, int mem_space_id,
		int file_space_id, int xfer_plist_id, byte[] buf)
		throws HDF5LibraryException, NullPointerException;

	/**
	 *  H5Dwrite writes a (partial) dataset, specified by its 
	 *  identifier dataset_id, from the application memory data 
	 *  object into the file.
	 *
	 *  @param dataset_id  Identifier of the dataset read from.
	 *  @param mem_type_id  Identifier of the memory datatype.
	 *  @param mem_space_id  Identifier of the memory dataspace.
	 *  @param file_space_id  Identifier of the dataset's dataspace 
	 *  in the file.
	 *  @param xfer_plist_id  Identifier of a transfer property 
	 *  list for this I/O operation.
	 *  @param obj Object with data to be written to the file.
	 * 
	 *  @return a non-negative value if successful
	 * 
	 *  @exception HDF5Exception - Failure in the data conversion.
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 *  @exception NullPointerException - data object is null.
	 **/
	public static int H5Dwrite(int dataset_id, int mem_type_id, int mem_space_id,
		int file_space_id, int xfer_plist_id, Object obj )
		throws HDF5Exception, 
		HDF5LibraryException, 
		NullPointerException
	{
		HDFArray theArray = new HDFArray(obj);
		byte[] buf = theArray.byteify();

		/* will raise exception on error */
		int status = H5Dwrite(dataset_id, mem_type_id, 
			mem_space_id, file_space_id, xfer_plist_id, buf);

		/* clean up these:  assign 'null' as hint to gc() */
		buf = null;
		theArray = null;
		return status;
	}


	/**
	 *  H5Dextend verifies that the dataset is at least of size size.
	 *
	 *  @param dataset_id  Identifier of the dataset.
	 *  @param size Array containing the new magnitude of each 
	 *  dimension.
	 *
	 *  @return a non-negative value if successful
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 *  @exception NullPointerException - size array is null.
	 **/
	public static native int H5Dextend(int dataset_id, long[] size)
		throws HDF5LibraryException, NullPointerException;


	/**
	 *  H5Dclose ends access to a dataset specified by dataset_id and
	 *  releases resources used by it.
	 *
	 *  @param dataset_id  Identifier of the dataset to finish 
	 *  access to.
	 *  
	 *  @return a non-negative value if successful
	 *  
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native int H5Dclose(int dataset_id) 
		throws HDF5LibraryException;

	// following static native functions are missing from HDF5 RM version 1.0.1

	/** H5Dget_storage_size returns the amount of storage that is 
	 *  required for the dataset.
	 *  
	 *  @param dataset_id  Identifier of the dataset in question 
	 *  
	 *  @return he amount of storage space allocated for the dataset.
	 *  
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native long H5Dget_storage_size(int dataset_id)
		throws HDF5LibraryException;


	//////////////////////////////////////////////////////////////
	//                                                          //
	//             H5F: File Interface Functions                //
	//                                                          //
	//////////////////////////////////////////////////////////////

	/**
	 *  H5Fopen opens an existing file and is the primary function 
	 *  for accessing existing HDF5 files.
	 *
	 *  @param name Name of the file to access.
	 *  @param flags File access flags.
	 *  @param access_id  Identifier for the file access properties 
	 *  list.
	 *
	 *  @return a file identifier if successful
	 *  
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 *  @exception NullPointerException - name is null.
	 **/
	public static native int H5Fopen(String name, int flags, 
		int access_id)
		throws HDF5LibraryException, NullPointerException;


	/**
	 *  H5Fcreate is the primary function for creating HDF5 files.
	 *
	 *  @param name Name of the file to access.
	 *  @param flags File access flags. Possible values include:
	 *  <UL>
	 *  <LI>
	 *      H5F_ACC_RDWR Allow read and write access to file.
	 *  </LI>
	 *  <LI>
	 *      H5F_ACC_RDONLY Allow read-only access to file.
	 *  </LI>
	 *  <LI>
	 *      H5F_ACC_TRUNC Truncate file, if it already exists, 
	 *      erasing all data previously stored in the file.
	 *  </LI>
	 *  <LI>
	 *      H5F_ACC_EXCL Fail if file already exists.
	 *  </LI>
	 *  <LI>
	 *      H5F_ACC_DEBUG Print debug information.
	 *  </LI>
	 *  <LI>
	 *      H5P_DEFAULT Apply default file access and creation 
	 *   properties.
	 *  </LI>
	 *  </UL>
	 *  
	 *  @param create_id  File creation property list identifier, 
	 *  used when modifying default file meta-data.
 	 *  Use H5P_DEFAULT for default access properties.
	 *  @param access_id File access property list identifier. 
	 *  If parallel file access is desired,
	 *  this is a collective call according to the communicator 
	 *  stored in the access_id (not supported in Java). 
 	 *  Use H5P_DEFAULT for default access properties.
 	 *  
	 *  @return a file identifier if successful
	 * 
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 *  @exception NullPointerException - name is null.
	 **/
	public static native int H5Fcreate(String name, int flags, 
		int create_id, int access_id)
		throws HDF5LibraryException, NullPointerException;


	/**
	 *  H5Fflush causes all buffers associated with a file or 
	 *  object to be immediately flushed (written) to disk 
	 *  without removing the data from the (memory) cache.  
	 *  <P>
	 *  After this call completes, the file (or object) is in 
	 *  a consistent state and all data written to date is 
	 *  assured to be permanent.
	 *
	 *  @param object_id  Identifier of object used to identify 
	 *  the file.  <b>object_id</b> can be any object associated 
	 *  with the file, including the file itself, a dataset, 
	 *  a group, an attribute, or a named data type. 

    scope specifies whether the scope of the flushing action is global or local. Valid values are 
                    H5F_SCOPE_GLOBAL
                                        
                                       Flushes the entire virtual file.
                    H5F_SCOPE_LOCAL
                                       Flushes only the specified file.
	 *  @param H5F_scope_t scope Specifies the scope of the 
	 *  flushing action, in the case that the HDF-5 file is not 
	 *  a single physical file.
	 *  <P>
	 *  Valid values are:
	 *  <UL>
	 *  <LI>
         *           H5F_SCOPE_GLOBAL
         *                               Flushes the entire virtual file.
	 *  </LI>
	 *  <LI>
         *          H5F_SCOPE_LOCAL
         *                             Flushes only the specified file.
	 *  </LI>
	 *  </UL>
	 *  
	 *  @return a non-negative value if successful
	 *  
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native int H5Fflush(int object_id, int scope) 
		throws HDF5LibraryException;


	/**
	 *  H5Fis_hdf5 determines whether a file is in the HDF5 format.
	 *
	 *  @param name File name to check format.
	 *
	 *  @return true if is HDF-5, false if not.
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 *  @exception NullPointerException - name is null.
	 **/
	public static native boolean H5Fis_hdf5(String name) 
		throws HDF5LibraryException, NullPointerException;


	/**
	 *  H5Fget_create_plist returns a file creation property list 
	 *  identifier identifying the creation properties used to 
	 *  create this file.
	 *
	 *  @param file_id  Identifier of the file to get creation 
	 *  property list
	 *
	 *  @return a file creation property list identifier if successful
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native int H5Fget_create_plist(int file_id) 
		throws HDF5LibraryException;


	/**
	 *  H5Fget_access_plist returns the file access property list 
	 *  identifier of the specified file.
	 *
	 *  @param file_id  Identifier of file to get access property 
	 *  list of
	 *  
	 *  @return a file access property list identifier if successful
	 *  
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native int H5Fget_access_plist(int file_id) 
		throws HDF5LibraryException;


	/**
	 *  H5Fclose terminates access to an HDF5 file.
	 *
	 *  @param file_id  Identifier of a file to terminate access to.
	 *
	 *  @return a non-negative value if successful
	 *  
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native int H5Fclose(int file_id) 
		throws HDF5LibraryException;


	/**
	 *  H5Fmount mounts the file specified by child_id onto the 
	 *  group specified by loc_id and name using the mount 
	 *  properties plist_id.
	 *
	 *  @param loc_id The identifier for the group onto which 
	 *  the file specified by child_id is to be mounted.
	 *  @param name  The name of the group onto which the file 
	 *  specified by child_id is to be mounted.
	 *  @param child_id The identifier of the file to be mounted.
	 *  @param plist_id The identifier of the property list to be used.
	 *
	 *  @return a non-negative value if successful
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 *  @exception NullPointerException - name is null.
	 **/
	public static native int H5Fmount(int loc_id, String name, 
		int child_id, int plist_id)
		throws HDF5LibraryException, NullPointerException;


	/**
	 *  Given a mount point, H5Funmount dissassociates the mount 
	 *  point's file from the file mounted there.
	 *
	 *  @param loc_id  The identifier for the location at which 
	 *  the specified file is to be unmounted.
	 *  @param name  The name of the file to be unmounted.
	 *
	 *  @return a non-negative value if successful
	 *  
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 *  @exception NullPointerException - name is null.
	 **/
	public static native int H5Funmount(int loc_id, String name)
		throws HDF5LibraryException, NullPointerException;


	/**
	 *  H5Freopen reopens an HDF5 file.
	 *
	 *  @param file_id  Identifier of a file to terminate and
	 *  reopen access to.
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 *  @return a new file identifier if successful
	 **/
	public static native int H5Freopen(int file_id) 
		throws HDF5LibraryException;


	//////////////////////////////////////////////////////////////
	//                                                          //
	//             H5G: Group Interface Functions               //
	//                                                          //
	//////////////////////////////////////////////////////////////

	/**
	 *  H5Gcreate creates a new group with the specified name at 
	 *  the specified location, loc_id.
	 *
	 *  @param loc_id The file or group identifier.
	 *  @param name The absolute or relative name of the new group.
	 *  @param size_hint An optional parameter indicating the 
	 *  number of bytes to reserve for the names that will appear 
	 *  in the group.
	 *  
	 *  @return a valid group identifier for the open group if 
	 *  successful
	 *  
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 *  @exception NullPointerException - name is null.
	 **/
	public static native int H5Gcreate(int loc_id, String name, 
		int size_hint)
		throws HDF5LibraryException, NullPointerException;


	/**
	 *  H5Gopen opens an existing group with the specified name 
	 *  at the specified location, loc_id.
	 *
	 *  @param loc_id File or group identifier within which group 
	 *  is to be open.
	 *  @param name Name of group to open.
	 *
	 *  @return a valid group identifier if successful
	 * 
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 *  @exception NullPointerException - name is null.
	 **/
	public static native int H5Gopen(int loc_id, String name)
		throws HDF5LibraryException, NullPointerException;


	/**
	 *  H5Gclose releases resources used by a group which was opened 
	 *  by a call to H5Gcreate() or H5Gopen().
	 *
	 *  @param group_id  Group identifier to release.
	 *
	 *  @return a non-negative value if successful
	 *  
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native int H5Gclose(int group_id) 
		throws HDF5LibraryException;


	/**
	 *  H5Glink creates a new name for an already existing object.
	 *
	 *  @param loc_id  File, group, dataset, or datatype identifier.
	 *  @param H5G_link_t link_type Link type. Possible values are:
	 *  <UL>
	 *  <LI> 
	 *  H5G_LINK_HARD 
	 *  </LI>
	 *  <LI>
	 *  H5G_LINK_SOFT.
	 *  </LI>
	 *  </UL>
	 *  @param current_name A name of the existing object if link is 
	 *  a hard link. Can be anything for the soft link.
	 *  @param new_name New name for the object.
	 *
	 *  @return a non-negative value if successful
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 *  @exception NullPointerException - current_name or name is null.
	 **/
	public static native int H5Glink(int loc_id, int link_type,
		String current_name, String new_name)
		throws HDF5LibraryException, NullPointerException;


	/**
	 *  H5Gunlink removes an association between a name and an object.
	 *
	 *  @param loc_id  Identifier of the file containing the object.
	 *  @param name Name of the object to unlink.
	 *
	 *  @return a non-negative value if successful
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 *  @exception NullPointerException - name is null.
	 **/
	public static native int H5Gunlink(int loc_id, String name)
		throws HDF5LibraryException, NullPointerException;

	// extensions to the standard interface:  not in the Ref. Man.

	/**
	 *  @name H5Gn_members  report the number of objects in
	 *        a Group.  The 'objects' include everything that
	 *        will be visited by H5Giterate.  Each link is
 	 *        returned, so objects with multiple links will
 	 *        be counted once for each link.
 	 *        
	 *  @param loc_id  file or group ID.
	 *  @param name   name of the group to iterate, relative to 
	 *  the loc_id
 	 *        
	 *  @returns the number of members in the group or -1 if error.
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 *  @exception NullPointerException - name is null.
	 */
	public static native int H5Gn_members( int loc_id, String name)
		throws HDF5LibraryException, NullPointerException;

	/**
	 *  @name H5Gget_obj_info_idx   report the name and type of
	 *        object with index 'idx' in a Group.  The 'idx'
	 *        corresponds to the index maintained by H5Giterate.  
	 *	  Each link is returned, so objects with multiple 
	 *        links will be counted once for each link.
 	 *        
	 *  @param loc_id  IN:  file or group ID.
	 *  @param name   IN:  name of the group to iterate, 
	 *   relative to the loc_id
	 *  @param idx   IN:  the index of the object to iterate.
	 *  @param oname  the name of the object [OUT]
	 *  @param type   the type of the object [OUT]
	 *
	 *  @returns non-negative if successful, -1 if not.
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 *  @exception NullPointerException - name is null.
	 */
	public static native int H5Gget_obj_info_idx( int loc_id, 
		String name, int idx, String[] oname, int[]type) 
		throws HDF5LibraryException, NullPointerException;

	/**
	 *  H5Gmove renames an object within an HDF5 file. 
	 *  The original name, src, is unlinked from the group graph 
	 *  and the new name, dst, is inserted as an atomic operation. 
	 *  Both names are interpreted relative to loc_id, which is
	 *  either a file or a group identifier.
	 *
	 *  @param loc_id File or group identifier.
	 *  @paramsrc Object's original name.
	 *  @paramdst Object's new name.
	 *
	 *  @return a non-negative value if successful
	 *  
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 *  @exception NullPointerException - src or dst is null.
	 **/
	public static native int H5Gmove(int loc_id, String src, 
		String dst)
		throws HDF5LibraryException, NullPointerException;


	/**
	 *  H5Gget_objinfo returns information about the specified 
	 *  object.
	 *
	 *  @param loc_id  IN: File, group, dataset, or datatype 
	 *  identifier.
	 *  @param name  IN: Name of the object for which status is 
	 *  being sought.
	 *  @param follow_link  IN: Link flag.
	 *  @param  fileno  OUT: file id numbers.
	 *  @param  objno  OUT: object id numbers.
	 *  @param  link_info  OUT: link information.
	 *      <pre>
	 *          link_info[0] = nlink
	 *          link_info[1] = type
	 *          link_info[2] = linklen
	 *      </pre>
	 *  @param  mtime  OUT: modification time
	 *
	 *  @return a non-negative value if successful, with the 
	 *  fields of link_info and mtime  (if non-null) initialized.
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 *  @exception NullPointerException - name or array is null.
	 *  @exception IllegalArgumentException - bad argument.
	 **/
	public static native int H5Gget_objinfo(int loc_id, String name, 
		boolean follow_link, long[] fileno, long[] objno, 
		int[] link_info, long[] mtime)
		throws HDF5LibraryException, 
		NullPointerException, 
		IllegalArgumentException;

	/**
	 *  H5Gget_objinfo returns information about the specified 
	 *  object in an HDF5GroupInfo object.
	 *
	 *  @param loc_id  IN: File, group, dataset, or datatype 
	 *  identifier.
	 *  @param name  IN: Name of the object for which status 
	 *  is being sought.
	 *  @param follow_link  IN: Link flag.
	 *  @param  info OUT: the HDF5GroupInfo object to store the 
	 *  object infomation
	 *
	 *  @return a non-negative value if successful, with the 
	 *  fields of HDF5GroupInfo object (if non-null) initialized. 
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 *  @exception NullPointerException - name is null.
	 *
	 *  @see ncsa.hdf.hdf5lib.HDF5GroupInfo
	 *  @see public static native int H5Gget_objinfo();
	 **/
	public static int H5Gget_objinfo(int loc_id, String name, 
		boolean follow_link,
		HDF5GroupInfo info) 
		throws HDF5LibraryException, NullPointerException
	{
		int status = -1;
		long[] fileno = new long[2];
		long[] objno = new long[2];
		int[] link_info = new int[3];
		long[] mtime = new long[1];

		status = H5Gget_objinfo(loc_id, name, follow_link,
			fileno, objno, link_info, mtime);

		if (status >=0 ) {
			info.setGroupInfo(fileno, objno, link_info[0],
			link_info[1], mtime[0], link_info[2]);
		}
		return status;
	}

	/**
	 *  H5Gget_linkval returns size characters of the link value 
	 *  through the value argument if loc_id (a file or group 
	 *  identifier) and name specify a symbolic link.
	 *
	 *  @param loc_id  IN: Identifier of the file, group, dataset, 
	 *  or datatype.
	 *  @param name  IN: Name of the object whose link value is to 
	 *  be checked.
	 *  @param size  IN: Maximum number of characters of value to 
	 *  be returned.
	 *  @param char *value  OUT: Link value.
	 *  
	 *  @return a non-negative value, with the link value in value, 
	 *  if successful.
	 *
	 *  @exception ArrayIndexOutOfBoundsException   Copy back failed
	 *  @exception ArrayStoreException  Copy back failed
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 *  @exception NullPointerException - name is null.
	 *  @exception IllegalArgumentException - size is invalid
	 **/
	public static native int H5Gget_linkval(int loc_id, String name, 
		int size, String[] value)
		throws ArrayIndexOutOfBoundsException, 
		ArrayStoreException,
		HDF5LibraryException, 
		NullPointerException, 
		IllegalArgumentException;


	/**
	 *  H5Gset_comment sets the comment for the the object name 
	 *  to comment.   Any previously existing comment is overwritten.
	 *
	 *  @param loc_id  IN: Identifier of the file, group, dataset, 
	 *  or datatype.
	 *  @param name  IN: Name of the object whose comment is to 
	 *  be set or reset.
	 *  @param comment  IN: The new comment.
	 *
	 *  @return a non-negative value if successful
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 *  @exception NullPointerException - name or comment is null.
	 **/
	public static native int H5Gset_comment(int loc_id, String name, 
		String comment)
		throws HDF5LibraryException, NullPointerException;


	/**
	 *  H5Gget_comment retrieves the comment for the the object name. 
	 *  The comment is returned in the buffer comment.
	 *
	 *  @param loc_id  IN: Identifier of the file, group, dataset, 
	 *  or datatype.
	 *  @param name  IN: Name of the object whose comment is to be 
	 *  set or reset.
	 *  @param bufsize  IN: Anticipated size of the buffer required 
	 *  to hold comment.
	 *  @param comment  OUT: The comment.
	 *  @return the number of characters in the comment, counting 
	 *  the null terminator, if successful
	 * 
	 *  @exception ArrayIndexOutOfBoundsException - JNI error writing
	 *  back data
	 *  @exception ArrayStoreException - JNI error writing
	 *  back data
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 *  @exception NullPointerException - name is null.
	 *  @exception IllegalArgumentException - size < 1, 
	 *  comment is invalid.
	 **/
	public static native int H5Gget_comment(int loc_id, String name, 
		int bufsize,
		String[] comment)
		throws ArrayIndexOutOfBoundsException, 
		ArrayStoreException,
		HDF5LibraryException, 
		NullPointerException, 
		IllegalArgumentException;


	//////////////////////////////////////////////////////////////
	//                                                          //
	//           H5I: Identifier Interface Functions            //
	//                                                          //
	//////////////////////////////////////////////////////////////

	/**
	 *  H5Iget_type retrieves the type of the object identified 
	 *  by obj_id.
	 *
	 *  @param obj_id  IN: Object identifier whose type is to be 
	 *  determined.
	 *
	 *  @return the object type if successful; otherwise H5I_BADID.
	 * 
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native int H5Iget_type(int obj_id) 
		throws HDF5LibraryException;


	//////////////////////////////////////////////////////////////
	//                                                          //
	//         H5P: Property List Interface Functions           //
	//                                                          //
	//////////////////////////////////////////////////////////////

	/**
	 *  H5Pcreate creates a new property as an instance of some 
	 *  property list class.
	 *
	 *  @param H5P_class_t type  IN: The type of property list 
	 *  to create.
	 *
	 *  @return a property list identifier (plist) if successful; 
	 *  otherwise Fail (-1).
	 * 
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native int H5Pcreate(int type) 
		throws HDF5LibraryException;


	/**
	 *  H5Pclose terminates access to a property list.
	 *
	 *  @param plist  IN: Identifier of the property list to 
	 *  terminate access to.
	 *  @return a non-negative value if successful
	 *  
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native int H5Pclose(int plist) 
		throws HDF5LibraryException;

	/**
	 *  H5Pget_class returns the property list class for the 
	 *  property list identified by the plist parameter.
	 *
	 *  @param plist  IN: Identifier of property list to query.
	 *  @return a property list class if successful. Otherwise 
	 *  returns H5P_NO_CLASS (-1).
	 *  
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native int H5Pget_class(int plist) 
		throws HDF5LibraryException;

	/**
	 *  H5Pcopy copies an existing property list to create a 
	 *  new property list.
	 *
	 *  @param plist  IN: Identifier of property list to duplicate.
	 *
	 *  @return a property list identifier if successful
	 *  
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native int H5Pcopy(int plist) 
		throws HDF5LibraryException;

	/**
	 *  H5Pget_version retrieves the version information of various 
	 *  objects for a file creation property list.
	 *
	 *  @param plist  IN: Identifier of the file creation property 
	 *  list.
	 *  @param version_info OUT: version information.
	 *  <pre>
	 *      version_info[0] = boot  // boot block version number
	 *      version_info[1] = freelist  // global freelist version 
	 *      version_info[2] = stab  // symbol tabl version number
	 *      version_info[3] = shhdr  // hared object header version 
	 *  </pre>
	 *  @return a non-negative value, with the values of version_info
	 *  initialized, if successful
	 *  
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 *  @exception NullPointerException - version_info is null.
	 *  @exception IllegalArgumentException - version_info is illegal.
	 **/
	public static native int H5Pget_version(int plist, 
		int[] version_info)
		throws HDF5LibraryException, 
		NullPointerException, 
		IllegalArgumentException;

	/**
	 *  H5Pset_userblock sets the user block size of a file 
	 *  creation property list.
	 *
	 *  @param plist  IN: Identifier of property list to modify.
	 *  @param size  IN: Size of the user-block in bytes.
	 *
	 *  @return a non-negative value if successful
	 *  
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native int H5Pset_userblock(int plist, long size)
		throws HDF5LibraryException;

	/**
	 *  H5Pget_userblock retrieves the size of a user block in a 
	 *  file creation property list.
	 *
	 *  @param plist  IN: Identifier for property list to query.
	 *  @param size  OUT: Pointer to location to return user-block 
	 *  size.
	 *
	 *  @return a non-negative value and the size of the user block;
	 *  if successful
	 * 
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 *  @exception NullPointerException - size is null.
	 **/
	public static native int H5Pget_userblock(int plist, long[] size)
		throws HDF5LibraryException, 
		NullPointerException;

	/**
	 *  H5Pset_sizes sets the byte size of the offsets and lengths 
	 *  used to address objects in an HDF5 file.
	 *
	 *  @param plist  IN: Identifier of property list to modify.
	 *  @param sizeof_addr  IN: Size of an object offset in bytes.
	 *  @param sizeof_size  IN: Size of an object length in bytes.
	 *
	 *  @return a non-negative value if successful
	 *  
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native int H5Pset_sizes(int plist, int sizeof_addr, 
		int sizeof_size)
		throws HDF5LibraryException;

	/**
	 *  H5Pget_sizes retrieves the size of the offsets and lengths 
	 *  used in an HDF5 file. This function is only valid for file 
	 *  creation property lists.
	 *
	 *  @param plist  IN: Identifier of property list to query.
	 *  @param size  OUT: the size of the offsets and length.
	 *  <pre>
	 *      size[0] = sizeof_addr // offset size in bytes
	 *      size[1] = sizeof_size // length size in bytes
	 *  </pre>
	 *  @return a non-negative value with the sizes initialized; 
	 *  if successful;
	 *  
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 *  @exception NullPointerException - size is null.
	 *  @exception IllegalArgumentException - size is invalid.
	 **/
	public static native int H5Pget_sizes(int plist, int[] size) 
		throws HDF5LibraryException, 
		NullPointerException, 
		IllegalArgumentException;

	/**
	 *  H5Pset_sym_k sets the size of parameters used to control the 
	 *  symbol table nodes.
	 *
	 *  @param plist  IN: Identifier for property list to query.
	 *  @param ik  IN: Symbol table tree rank.
	 *  @param lk  IN: Symbol table node size.
	 *
	 *  @return a non-negative value if successful
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native int H5Pset_sym_k(int plist, int ik, int lk) 
		throws HDF5LibraryException;


	/**
	 *  H5Pget_sym_k retrieves the size of the symbol table 
	 *  B-tree 1/2 rank and the symbol table leaf node 1/2 size.
	 *
	 *  @param plist  IN: Property list to query.
	 *  @param size  OUT: the symbol table's B-tree 1/2 rank 
	 *  and leaf node 1/2 size.
	 *  <pre>
	 *      size[0] = ik // the symbol table's B-tree 1/2 rank
	 *      size[1] = lk // leaf node 1/2 size
	 *  </pre>
	 *
	 *  @return a non-negative value if successful
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 *  @exception NullPointerException - size is null.
	 *  @exception IllegalArgumentException - size is invalid.
	 **/
	public static native int H5Pget_sym_k(int plist, int[] size) 
		throws HDF5LibraryException, 
		NullPointerException, 
		IllegalArgumentException;


	/**
	 *  H5Pset_istore_k sets the size of the parameter used to 
	 *  control the B-trees for indexing chunked datasets.
	 *
	 *  @param plist  IN: Identifier of property list to query.
	 *  @param ik  IN: 1/2 rank of chunked storage B-tree.
	 *
	 *  @return a non-negative value if successful
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native int H5Pset_istore_k(int plist, int ik)
		throws HDF5LibraryException;


	/**
	 *  H5Pget_istore_k queries the 1/2 rank of an indexed 
	 *  storage B-tree.
	 *
	 *  @param plist  IN: Identifier of property list to query.
	 *  @param ik  OUT: Pointer to location to return the chunked 
	 *  storage B-tree 1/2 rank.
	 *
	 *  @return a non-negative value if successful
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 *  @exception NullPointerException - ik array is null.
	 **/
	public static native int H5Pget_istore_k(int plist, int[] ik)
		throws HDF5LibraryException, 
		NullPointerException;


	/**
	 *  H5Pset_layout sets the type of storage used store the 
	 *  raw data for a dataset.
	 *
	 *  @param plist  IN: Identifier of property list to query.
	 *  @param layout  IN: Type of storage layout for raw data.
	 *
	 *  @return a non-negative value if successful
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native int H5Pset_layout(int plist, int layout)
		throws HDF5LibraryException;


	/**
	 *  H5Pget_layout returns the layout of the raw data for a dataset.
	 *
	 *  @param plist  IN: Identifier for property list to query.
	 *
	 *  @return the layout type of a dataset creation property 
	 *  list if successful.
	 *  Otherwise returns H5D_LAYOUT_ERROR (-1).
	 * 
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native int H5Pget_layout(int plist) 
		throws HDF5LibraryException;


	/**
	 *  H5Pset_chunk sets the size of the chunks used to store a 
	 *  chunked layout dataset.
	 *
	 *  @param plist  IN: Identifier for property list to query.
	 *  @param ndims  IN: The number of dimensions of each chunk.
	 *  @param dim  IN: An array containing the size of each chunk.
	 *
	 *  @return a non-negative value if successful
	 * 
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 *  @exception NullPointerException - dims array is null.
	 *  @exception IllegalArgumentException - dims <=0
	**/
	public static native int H5Pset_chunk(int plist, int ndims, 
		long[] dim)
		throws HDF5LibraryException, 
		NullPointerException, 
		IllegalArgumentException;


	/**
	 *  H5Pget_chunk retrieves the size of chunks for the raw 
	 *  data of a chunked layout dataset.
	 *
	 *  @param plist  IN: Identifier of property list to query.
	 *  @param max_ndims  OUT: Size of the dims array.
	 *  @param dims  OUT: Array to store the chunk dimensions.
	 *
	 *  @return chunk dimensionality successful
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 *  @exception NullPointerException - dims array is null.
	 *  @exception IllegalArgumentException - max_ndims <=0
	 **/
	public static native int H5Pget_chunk(int plist, int max_ndims, 
		long[] dims)
		throws HDF5LibraryException, 
		NullPointerException, 
		IllegalArgumentException;


	/**
	 *  H5Pset_alignment sets the alignment properties of a 
	 *  file access property list so that any file object >= 
	 *  THRESHOLD bytes will be aligned on an address which 
	 *  is a multiple of ALIGNMENT.
	 *
	 *  @param plist  IN: Identifier for a file access property list.
	 *  @param threshold  IN: Threshold value.
	 *  @param alignment  IN: Alignment value.
	 *
	 *  @return a non-negative value if successful
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native int H5Pset_alignment(int plist, 
		long threshold, long alignment)
		throws HDF5LibraryException;


	/**
	 *  H5Pget_alignment retrieves the current settings for 
	 *  alignment properties from a file access property list.
	 *
	 *  @param plist  IN: Identifier of a file access property list.
	 *  @param alignment  OUT: threshold value and alignment value.
	 *  <pre>
	 *      alignment[0] = threshold // threshold value
	 *      alignment[1] = alignment // alignment value
	 *  </pre>
	 *  @return a non-negative value if successful
	 * 
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 *  @exception NullPointerException - aligment array is null.
	 *  @exception IllegalArgumentException - aligment array is 
	 *  invalid.
	 **/
	public static native int H5Pget_alignment(int plist, 
		long[] alignment)
		throws HDF5LibraryException, 
		NullPointerException, 
		IllegalArgumentException;

	/**
	 *  H5Pset_external adds an external file to the list of 
	 *  external files.
	 *
	 *  @param plist  IN: Identifier of a dataset creation property 
	 *  list.
	 *  @param name  IN: Name of an external file.
	 *  @param offset  IN: Offset, in bytes, from the beginning 
	 *  of the file to the location in the file where the 
	 *  data starts.
	 *  @param size  IN: Number of bytes reserved in the file for 
	 *  the data.
	 *
	 *  @return a non-negative value if successful
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 *  @exception NullPointerException - name is null.
	 **/
	public static native int H5Pset_external(int plist, String name, 
		long offset, long size)
		throws HDF5LibraryException, NullPointerException;


	/**
	 *  H5Pget_external_count returns the number of external 
	 *  files for the specified dataset.
	 *
	 *  @param plist  IN: Identifier of a dataset creation property 
	 *  list.
	 *
	 *  @return the number of external files if successful
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native int H5Pget_external_count(int plist) 
		throws HDF5LibraryException;


	/**
	 *  H5Pget_external returns information about an external file.
	 *
	 *  @param plist  IN: Identifier of a dataset creation property 
	 *  list.
	 *  @param idx  IN: External file index.
	 *  @param name_size  IN: Maximum length of name array.
	 *  @param name  OUT: Name of the external file.
	 *  @param size  OUT: the offset value and the size of 
	 *  the external file data.
	 *  <pre>
	 *      size[0] = offset // a location to return an offset value
	 *      size[1] = size // a location to return the size of 
	 *                // the external file data.
	 *  </pre>
	 *
	 *  @return a non-negative value if successful
	 *
	 *  @exception ArrayIndexOutOfBoundsException  Fatal 
	 *  error on Copyback 
	 *  @exception ArrayStoreException  Fatal error on Copyback
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 *  @exception NullPointerException - name or size is null.
	 *  @exception IllegalArgumentException - name_size <= 0 .
	 *
	 **/
	public static native int H5Pget_external(int plist, int idx, 
		int name_size, String[] name, long[] size)
		throws ArrayIndexOutOfBoundsException, 
		ArrayStoreException,
		HDF5LibraryException, 
		NullPointerException, 
		IllegalArgumentException;


	/**
	 *  H5Pset_fill_value sets the fill value for a dataset creation 
	 *  property list.  <b>NOT IMPLEMENTED YET </b>
	 *
	 *  @param plist_id  IN: Property list identifier.
	 *  @param type_id,  IN: The datatype identifier of value.
	 *  @param value  IN: The fill value.
	 *
	 *  @return a non-negative value if successful
	 *
	 *  @exception HDF5Exception - Error converting data array
	 **/
	public static native int H5Pset_fill_value(int plist_id, 
		int type_id, byte[] value)
		throws HDF5Exception;

	/**
	 *  H5Pset_fill_value sets the fill value for a dataset creation 
	 *  property list.
	 *
	 *  @param plist_id  IN: Property list identifier.
	 *  @param type_id,  IN: The datatype identifier of value.
	 *  @param obj  IN: The fill value.
	 *
	 *  @return a non-negative value if successful
	 *
	 *  @exception HDF5Exception - Error converting data array
	 **/
	public static int H5Pset_fill_value(int plist_id, 
		int type_id, Object obj)
		throws HDF5Exception
	{
		HDFArray theArray = new HDFArray(obj);
		byte[] buf = theArray.byteify();

		int retVal = H5Pset_fill_value(plist_id, type_id, buf);

		buf = null;
		theArray = null;
		return retVal;
	}

	/**
	 *  H5Pget_fill_value queries the fill value property of a dataset
	 *  creation property list. <b>NOT IMPLEMENTED YET</B>
	 *
	 *  @param plist_id IN: Property list identifier.
	 *  @param type_id IN: The datatype identifier of value.
	 *  @param value  IN: The fill value.
	 *
	 *  @return a non-negative value if successful
	 *  
	 **/
	public static native int H5Pget_fill_value(int plist_id, 
		int type_id, byte[] value)
		throws HDF5Exception;

	/**
	 *  H5Pget_fill_value queries the fill value property of a dataset
	 *  creation property list. <b>NOT IMPLEMENTED YET</B>
	 *
	 *  @param plist_id IN: Property list identifier.
	 *  @param type_id IN: The datatype identifier of value.
	 *  @param obj  IN: The fill value.
	 *
	 *  @return a non-negative value if successful
	 *  
	 **/
	public static int H5Pget_fill_value(int plist_id, int type_id, Object obj)
		throws HDF5Exception
	{
		HDFArray theArray = new HDFArray(obj);
		byte[] buf = theArray.emptyBytes();

		int status = H5Pget_fill_value(plist_id, type_id, buf);
		if (status >= 0) obj = theArray.arrayify( buf);

		return status;
	}


	/**
	 *  H5Pset_filter adds the specified filter and corresponding
	 *  properties to the end of an output filter pipeline.
	 *
	 *  @param plist IN: Property list identifier.
	 *  @param_t filter IN: Filter to be added to the pipeline.
	 *  @param flags IN: Bit vector specifying certain general 
	 *  properties of the filter.
	 *  @param cd_nelmts IN: Number of elements in cd_values
	 *  @param cd_values[] IN: Auxiliary data for the filter.
	 *
	 *  @return a non-negative value if successful
	 *  
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native int H5Pset_filter(int plist, int filter, 
		int flags, int cd_nelmts, int[] cd_values) 
		throws HDF5LibraryException;

	/**
	 *  H5Pget_nfilters returns the number of filters defined in 
	 *  the filter pipeline associated with the property list plist.
	 *
	 *  @param plist IN: Property list identifier.
	 *
	 *  @return the number of filters in the pipeline if successful
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native int H5Pget_nfilters(int plist) 
		throws HDF5LibraryException;

	/**
	 *  H5Pget_filter returns information about a filter, specified 
	 *  by its filter number, in a filter pipeline, specified by 
	 *  the property list with which it is associated.
	 *
	 *  @param plist IN: Property list identifier.
	 *  @param filter_number IN: Sequence number within the 
	 *  filter pipeline of the filter for which information is sought.
	 *  @param flags OUT: Bit vector specifying certain general 
	 *  properties of the filter.
	 *  @param cd_nelmts IN/OUT: Number of elements in cd_values
	 *  @param cd_values OUT: Auxiliary data for the filter.
	 *  @param namelen IN: Anticipated number of characters in name.
	 *  @param name[] OUT: Name of the filter.
	 *
	 *  @return the filter identification number if successful. Otherwise
	 *    returns H5Z_FILTER_ERROR (-1).
	 *
	 *  @exception ArrayIndexOutOfBoundsException  
	 *  Fatal error on Copyback 
	 *  @exception ArrayStoreException  Fatal error on Copyback
	 *  @exception NullPointerException - name or an array is null.
	 *
	 **/
	public static native int H5Pget_filter(int plist, 
		int filter_number, int[] flags,
		int[] cd_nelmts, int[] cd_values, 
		int namelen, String[] name)
		throws ArrayIndexOutOfBoundsException, 
		ArrayStoreException,
		HDF5LibraryException, 
		NullPointerException ;


	/**
	 *  H5Pget_driver returns the identifier of the low-level 
	 *  file driver.
	 *  <p>
	 *  Valid identifiers are:
	 *  <UL>
	 *  <LI>
	 *       H5F_LOW_STDIO (0)
	 *  </LI>
	 *  <LI>
	 *       H5F_LOW_SEC2 (1)
	 *  </LI>
	 *  <LI>
	 *       H5F_LOW_MPIO (2)
	 *  </LI>
	 *  <LI>
	 *       H5F_LOW_CORE (3)
	 *  </LI>
	 *  <LI>
	 *       H5F_LOW_SPLIT (4)
	 *  </LI>
	 *  <LI>
	 *       H5F_LOW_FAMILY (5)
	 *  </LI>
	 *  </UL>
	 *
	 *  @param plist IN: Identifier of a file access property list.
	 *
	 *  @return a low-level driver identifier if successful. Otherwise returns
	 *  H5F_LOW_ERROR (-1).
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native int H5Pget_driver(int plist) 
		throws HDF5LibraryException;

	/**
	 *  H5Pset_stdio sets the low level file driver to use the 
	 *  functions declared in the stdio.h file: fopen(), fseek() 
	 *  or fseek64(), fread(), fwrite(), and fclose().
	 *
	 *  @param plist IN: Identifier of a file access property list.
	 *
	 *  @return a non-negative value if successful
	 *
	 **/
	public static native int H5Pset_stdio(int plist) 
		throws HDF5LibraryException;

	/**
	 *  H5Pget_stdio checks to determine whether the file access
	 *  property list is set to the stdio driver.
	 *
	 *  @param plist IN: Identifier of a file access property list.
	 *  @return true if the file access property list is set to
	 *    the stdio driver. Otherwise returns a negative value.
	 *
	 **/
	public static native boolean H5Pget_stdio(int plist);

	/**
	 *  H5Pset_sec2 sets the low-level file driver to use the 
	 *  functions declared in the unistd.h file: open(), lseek() 
	 *  or lseek64(), read(), write(), and close().
	 *
	 *  @param plist IN: Identifier of a file access property list.
	 *  @return a non-negative value if successful
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native int H5Pset_sec2(int plist) 
		throws HDF5LibraryException;


	/**
	 *  H5Pget_sec2 checks to determine whether the file access 
	 *  property list is set to the sec2 driver.
	 *
	 *  @param plist IN: Identifier of a file access property list.
	 *  @return true if the file access property list is set to
	 *  the sec2 driver. Otherwise returns a negative value.
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native boolean H5Pget_sec2(int plist) 
		throws HDF5LibraryException;

	/**
	 *  H5Pset_core sets the low-level file driver to use malloc() and
	 *  free().
	 *
	 *  @param plist IN: Identifier of a file access property list.
	 *  @param increment IN: File block size in bytes.
	 *
	 *  @return a non-negative value if successful
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native int H5Pset_core(int plist, int increment)
		throws HDF5LibraryException;


	/**
	 *  H5Pget_core checks to determine whether the file access 
	 *  property list is set to the core driver.
	 *
	 *  @param plist IN: Identifier of the file access property list.
	 *  @param increment OUT: A location to return the file block size
	 *  @return true if the file access property list is set to
	 *    the core driver.
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native boolean H5Pget_core(int plist, 
		int[] increment)
		throws HDF5LibraryException;


	/**
	 *  H5Pset_split sets the low-level driver to split meta data 
	 *  from raw data, storing meta data in one file and raw data 
	 *  in another file.
	 *
	 *  @param plist  IN: Identifier of the file access property list.
	 *  @param meta_ext  IN: Name of the extension for the metafile 
	 *  filename.   Recommended default value: <i>.meta</i>.
	 *  @param meta_plist  IN: Identifier of the meta file access 
	 *  property list.
	 *  @param raw_ext  IN: Name extension for the raw file filename. 
	 *  Recommended default value: <i>.raw</i>.
	 *  @param raw_plist  IN: Identifier of the raw file access 
	 *  property list.
	 *  @return a non-negative value if successful
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 *  @exception NullPointerException - a string is null.
	 **/
	public static native int H5Pset_split(int plist, String meta_ext, 
		int meta_plist, String raw_ext, int raw_plist)
		throws HDF5LibraryException,
		NullPointerException;


	/**
	 *  H5Pget_split checks to determine whether the file access
	 *  property list is set to the split driver.
	 *
	 *  @param plist  IN: Identifier of the file access property list.
	 *  @param meta_ext_size  IN: Number of characters of the 
	 *  meta file extension to be copied to the meta_ext buffer.
	 *  @param meta_ext  IN: Meta file extension.
	 *  @param *meta_properties OUT: A copy of the meta file 
	 *  access property list.
	 *  @param raw_ext_size  IN: Number of characters of the 
	 *  raw file extension to be copied to the raw_ext buffer.
	 *  @param raw_ext OUT: Raw file extension.
	 *  @param *raw_properties OUT: A copy of the raw file 
	 *  access property list.
	 *
	 *  @return true if the file access property list is set to
	 *      the split driver.
	 *
         *  @exception ArrayIndexOutOfBoundsException  JNI error 
	 *  writing back array
         *  @exception ArrayStoreException   JNI error writing back array
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 *  @exception NullPointerException - a string or array is null.
	 **/
	public static native boolean H5Pget_split(int plist, 
		int meta_ext_size, String[] meta_ext,
		int[] meta_properties, int raw_ext_size, 
		String[] raw_ext, int[] raw_properties) 
		throws ArrayIndexOutOfBoundsException,  
		ArrayStoreException, 
		HDF5LibraryException, 
		NullPointerException;

	/**
	 *  H5Pset_family sets the file access properties to use the 
	 *  family driver; any previously defined driver properties 
	 *  are erased from the  property list. 
	 *
	 *  @param plist  IN: Identifier of the file access property list.
	 *  @param memb_size  IN: Logical size, in bytes, of each 
	 *  family member.
	 *  @param memb_plist  IN: Identifier of the file access 
	 *  property list for each member of the family.
	 *  @return a non-negative value if successful
	 * 
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native int H5Pset_family(int plist, long memb_size, 
		int memb_plist)
		throws HDF5LibraryException;


	/**
	 *  H5Pget_family checks to determine whether the file access
	 *  property list is set to the family driver.
	 *
	 *  @param plist  IN: Identifier of the file access property list.
	 *  @param memb_size OUT: Logical size, in bytes, of each 
	 *  family member.
	 *  @param *memb_plist OUT: Identifier of the file access 
	 *  property list for each member of the family.
	 *
	 *  @return a non-negative value if the file access property 
	 *  list is set to the family driver.
	 * 
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 *  @exception NullPointerException - an array is null.
	 **/
	public static native int H5Pget_family(int tid, long[] memb_size, 
		int[] memb_plist)
		throws HDF5LibraryException, NullPointerException;


	/**
	 *  H5Pset_cache sets the number of elements (objects) in the meta
	 *  data cache and the total number of bytes in the raw data chunk
	 *  cache.
	 *
	 *  @param plist  IN: Identifier of the file access property list.
	 *  @param mdc_nelmts  IN: Number of elements (objects) in the 
	 *  meta data cache.
	 *  @param rdcc_nbytes  IN: Total size of the raw data chunk 
	 *  cache, in bytes.
	 *  @param rdcc_w0  IN: Preemption policy.
	 *
	 *  @return a non-negative value if successful
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native int H5Pset_cache(int plist, int mdc_nelmts,
		int rdcc_nelmts, int rdcc_nbytes, double rdcc_w0)
		throws HDF5LibraryException;

	/**
	 *  Retrieves the maximum possible number of elements in the meta
	 *  data cache and the maximum possible number of bytes and the
	 *  RDCC_W0 value in the raw data chunk cache.
	 *
	 *  @param plist  IN: Identifier of the file access property list.
	 *  @param mdc_nelmts IN/OUT: Number of elements (objects) in 
	 *  the meta data cache.
	 *  @param rdcc_nbytes IN/OUT: Total size of the raw data 
	 *  chunk cache, in bytes.
	 *  @param rdcc_w0 IN/OUT: Preemption policy.
	 *
	 *  @return a non-negative value if successful
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 *  @exception NullPointerException - an array is null.
	 **/
	public static native int H5Pget_cache(int plist, int[] mdc_nelmts,
		int[] rdcc_nelmts, int[] rdcc_nbytes, double[] rdcc_w0)
		throws HDF5LibraryException, NullPointerException;

	/**
	 *  H5Pset_preserve sets the dataset transfer property list 
	 *  status to TRUE or FALSE.
	 *
	 *  @param plist  IN: Identifier for the dataset transfer 
	 *  property list.
	 *  @param status  IN: Status of for the dataset transfer 
	 *  property list.
	 *
	 *  @return a non-negative value if successful
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 *  @exception IllegalArgumentException - plist is invalid.
	 **/
	public static native int H5Pset_preserve(int plist, 
		boolean status) 
		throws HDF5LibraryException, IllegalArgumentException;


	/**
	 *  H5Pget_preserve checks the status of the dataset transfer
	 *  property list.
	 *
	 *  @param plist  IN: Identifier for the dataset transfer 
	 *  property list.
	 *
	 *  @return TRUE or FALSE if successful
	 * 
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native int H5Pget_preserve(int plist) 
		throws HDF5LibraryException;

	/**
	 *  H5Pset_deflate sets the compression method for a dataset.
	 *
	 *  @param plist  IN: Identifier for the dataset creation 
	 *  property list.
	 *  @param level  IN: Compression level.
	 *
	 *  @return true if successful
	 * 
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native boolean H5Pset_deflate(int plist, int level)
		throws HDF5LibraryException;

	// The following API functions were added since HDF5 version 1.0.1
	// The java wrappers are only partly tested.
	/**
	 * H5Pset_gc_references Sets the flag for garbage collecting 
	 * references for the file.  Default value for garbage 
	 * collecting references is off.
	 * 
	 *  @param fapl_id  IN File access property list
	 *  @param gc_ref  IN set GC on  (true) or off (false)
	 * 
	 *  @return non-negative if successful
	 * 
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native int  H5Pset_gc_references(int fapl_id, 
		boolean gc_ref)
		throws HDF5LibraryException;


	/**
	 *  H5Pget_gc_references Returns the current setting for the 
	 *  garbage collection refernces property from a file 
	 *  access property list.
	 *
	 *  @param fapl_id  IN File access property list
	 *  @param gc_ref  OUT GC is on  (true) or off (false)
	 * 
	 *  @return non-negative if succeed
	 * 
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 *  @exception NullPointerException - array is null.
	 **/
	public static native int H5Pget_gc_reference(int fapl_id, 
		boolean[] gc_ref)
		throws HDF5LibraryException, 
		NullPointerException;

	/**
	 *  H5Pset_hyper_cache Indicates whether to cache hyperslab 
	 *  blocks during I/O. 
	 *  <P>
	 *  Given a dataset transfer property list, 
	 *  H5Pset_hyper_cache indicates whether to cache
	 *  hyperslab blocks during I/O, a process which can 
	 *  significantly increase I/O speeds. 
	 *
	 *  @param plist_id  IN Dataset transfer property list
	 *  @param cache  IN  cache on (true)/off (false)
	 *  @param limit  IN   Maximum size of the hyperslab block 
	 *  to cache. 0 (zero) indicates no limit. 
	 * 
	 *  @return non-negative if succeed
	 * 
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native int H5Pset_hyper_cache(int plist_id, 
		boolean cache, int limit)
		throws HDF5LibraryException;

	/**
	 *  H5Pget_hyper_cache Find whether to hyperslab 
	 *  blocks are cached during I/O. 
	 *
	 *  @param plist_id  IN Dataset transfer property list
	 *  @param cache  OUT  cache on (true)/off (false)
	 *  @param limit  OUT   Maximum size of the hyperslab block 
	 *  to cache. 0 (zero) indicates no limit. 
	 * 
	 *  @return non-negative if succeed
	 * 
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 *  @exception NullPointerException - an array is null.
	 **/
	public static native int H5Pget_hyper_cache(int plist_id, 
		boolean[] cache, int[] limit)
		throws HDF5LibraryException, NullPointerException;

	/**
	 *  H5Pset_btree_ratio Sets B-tree split ratios for a dataset 
	 *  transfer property list. The split ratios determine what 
	 *  percent of children go in the first node when a node splits.
	 *
	 *  @param plist_id  IN Dataset transfer property list
	 *  @param left  IN  split ratio for leftmost nodes
	 *  @param right  IN  split ratio for righttmost nodes
	 *  @param middle  IN  split ratio for all other nodes
	 * 
	 *  @return non-negative if succeed
	 * 
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native int H5Pset_btree_ratios(int plist_id, 
		double left, double middle, double right)
		throws HDF5LibraryException;

	/**
	 *  H5Pget_btree_ratio Get the B-tree split ratios for a dataset 
	 *  transfer property list. 
	 *
	 *  @param plist_id  IN Dataset transfer property list
	 *  @param left  OUT  split ratio for leftmost nodes
	 *  @param right  OUT  split ratio for righttmost nodes
	 *  @param middle  OUT  split ratio for all other nodes
	 * 
	 *  @return non-negative if succeed
	 * 
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 *  @exception NullPointerException - an input array is null.
	 **/
	public static native int H5Pget_btree_ratios(int plist_id, 
		double[] left, double[] middle, double[] right)
		throws HDF5LibraryException, NullPointerException;


	//////////////////////////////////////////////////////////////
	//                                                          //
	//          H5R: Reference Interface Functions              //
	//                                                          //
	//////////////////////////////////////////////////////////////

	private static native int H5Rcreate(byte[] ref,
		int loc_id, String name, int ref_type, int space_id)
		throws HDF5LibraryException, 
		NullPointerException, 
		IllegalArgumentException;

	/**
	 *  H5Rcreate creates the reference, ref, of the type specified in
	 *  ref_type, pointing to the object name located at loc_id.
	 *
	 *  @param loc_id  IN: Location identifier used to locate 
	 *  the object being pointed to.
	 *  @param name  IN: Name of object at location loc_id.
	 *  @param ref_type  IN: Type of reference.
	 *  @param space_id  IN: Dataspace identifier with selection.
	 *
	 *  @return the reference (byte[]) if successful
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 *  @exception NullPointerException - an input array is null.
	 *  @exception IllegalArgumentException - an input array is 
	 *  invalid.
	 **/
	public static byte[] H5Rcreate(
		int loc_id, String name, int ref_type, int space_id)
		throws HDF5LibraryException, 
		NullPointerException, 
		IllegalArgumentException 
	{
		/*  These sizes are correct for HDF5.1.2 */
		int ref_size = 8;
		if (ref_type == HDF5Constants.H5R_DATASET_REGION) {
			ref_size = 12;
		}
		byte rbuf[] = new byte[ref_size];

		/*  will raise an exception if fails  */
		H5Rcreate(rbuf, loc_id, name, ref_type, space_id);

		return rbuf;
	}

	/**
	 *  Given a reference to some object, H5Rdereference opens 
	 *  that object and return an identifier.
	 *
	 *  @param dataset  IN: Dataset containing reference object.
	 *  @paramete ref_type  IN: The reference type of ref.
	 *  @param ref  IN: reference to an object 
	 *
	 *  @return valid identifier if successful
	 *  
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 *  @exception NullPointerException - output array is null.
	 *  @exception IllegalArgumentException - output array is invalid.
	**/
	public static native int H5Rdereference(int dataset, 
		int ref_type, byte[] ref)
		throws HDF5LibraryException, 
		NullPointerException, 
		IllegalArgumentException;

	/**
	 *  Given a reference to an object ref, H5Rget_region creates 
	 *  a copy of the dataspace of the dataset pointed to and 
	 *  defines a selection in the copy which is the region 
	 *  pointed to.
	 *
	 *  @param loc_id,  IN: loc_id  of the reference object.
	 *  @param ref_type,  IN: The reference type of ref.
	 *  @param reference  OUT: the reference to the object and region
	 *
	 *  @return a valid identifier if successful
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 *  @exception NullPointerException - output array is null.
	 *  @exception IllegalArgumentException - output array is invalid.
	 **/
	public static native int H5Rget_region(int loc_id, int ref_type, 
		byte[] ref )
		throws HDF5LibraryException, 
		NullPointerException, 
		IllegalArgumentException;

	/**
	 *  Given a reference to an object ref, H5Rget_object_type 
	 *  returns the type of the object pointed to. 
	 *
	 *  @param loc_id,  IN: loc_id  of the reference object.
	 *  @param ref  IN: the reference
	 *
	 *  @return a valid identifier if successful
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 *  @exception NullPointerException - array is null.
	 *  @exception IllegalArgumentException - array is invalid.
	 **/
	public static native int H5Rget_object_type(int loc_id, 
		byte ref[])
		throws HDF5LibraryException, 
		NullPointerException, 
		IllegalArgumentException;


	//////////////////////////////////////////////////////////////
	//                                                          //
	//          H5S: Dataspace Interface Functions              //
	//                                                          //
	//////////////////////////////////////////////////////////////

	/**
	 *  H5Screate creates a new dataspace of a particular type.
	 *
	 *  @param type The type of dataspace to be created.
	 *
	 *  @return a dataspace identifier if successful
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native int H5Screate(int type) 
		throws HDF5LibraryException;


	/**
	 *  H5Screate_simple creates a new simple data space and opens 
	 *  it for access.
	 *
	 *  @param rank Number of dimensions of dataspace.
	 *  @param dims An array of the size of each dimension.
	 *  @param maxdims An array of the maximum size of each dimension.
	 *
	 *  @return a dataspace identifier if successful
	 *  
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 *  @exception NullPointerException - dims or maxdims is null.
	 **/
	public static native int H5Screate_simple(int rank, long[] dims, 
		long[] maxdims)
		throws HDF5LibraryException, 
		NullPointerException;

	/**
	 *  H5Scopy creates a new dataspace which is an exact copy of the
	 *  dataspace identified by space_id.
	 *
	 *  @param space_id  Identifier of dataspace to copy.
	 *  @return a dataspace identifier if successful
	 *  
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native int H5Scopy(int space_id) 
		throws HDF5LibraryException;

	/**
	 *  H5Sselect_elements selects array elements to be included in the
	 *  selection for the space_id dataspace.
	 *
	 *  @param space_id  Identifier of the dataspace.
	 *  @param op operator specifying how the new selection is 
	 *  combined.
	 *  @param num_elements Number of elements to be selected.
	 *  @param coord A 2-dimensional array specifying the 
	 *  coordinates of the elements.
	 *
	 *  @return a non-negative value if successful
	 *  
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	private static native int H5Sselect_elements(int space_id, int op, 
		int num_elements, byte[] coord)
		throws HDF5LibraryException, 
		NullPointerException;

	/**
	 *  H5Sselect_elements selects array elements to be included in the
	 *  selection for the space_id dataspace.
	 *
	 *  @param space_id  Identifier of the dataspace.
	 *  @param op operator specifying how the new selection is 
	 *  combined.
	 *  @param num_elements Number of elements to be selected.
	 *  @param coord A 2-dimensional array specifying the 
	 *  coordinates of the elements.
	 *
	 *  @return a non-negative value if successful
	 *  
	 *  @exception HDF5Exception - Error in the data conversion
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 *  @exception NullPointerException - cord array is
	 **/
	public static int H5Sselect_elements(int space_id, int op, 
		int num_elements, long[][] coord2D)
		throws HDF5Exception, 
		HDF5LibraryException, 
		NullPointerException
	{
		if (coord2D == null) return -1;

		HDFArray theArray = new HDFArray((Object)coord2D);
		byte[] coord = theArray.byteify();

		int retVal = H5Sselect_elements(space_id, op, 
			num_elements, coord);

		coord = null;
		theArray = null;
		return retVal;
	}

	/**
	 *  H5Sselect_all selects the entire extent of the 
	 *  dataspace space_id.
	 *
	 *  @param space_id  IN: The identifier of the dataspace 
	 *  to be selected.
	 *
	 *  @return a non-negative value if successful
	 *  
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native int H5Sselect_all(int space_id) 
		throws HDF5LibraryException;


	/**
	 *  H5Sselect_none resets the selection region for the 
	 *  dataspace space_id to include no elements.
	 *
	 *  @param space_id  IN: The identifier of the dataspace to 
	 *  be reset.
	 *  @return a non-negative value if successful
	 *  
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native int H5Sselect_none(int space_id) 
		throws HDF5LibraryException;

	/**
	 *  H5Sselect_valid verifies that the selection for the dataspace.
	 *
	 *  @param space_id  The identifier for the dataspace in 
	 *  which the selection is being reset.
	 *
	 *  @return true if the selection is contained within 
	 *  the extent and FALSE if it is not or is an error. 
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native boolean H5Sselect_valid(int space_id) 
		throws HDF5LibraryException;


	/**
	 *  H5Sget_simple_extent_npoints determines the number of elements
	 *  in a dataspace.
	 *
	 *  @param space_id ID of the dataspace object to query
	 *  @return the number of elements in the dataspace if successful
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native long H5Sget_simple_extent_npoints(
		int space_id)
		throws HDF5LibraryException;


	/**
	 *  H5Sget_select_npoints determines the number of elements in the
	 *  current selection of a dataspace.
	 *
	 *  @param space_id Dataspace identifier.
	 *
	 *  @return the number of elements in the selection if successful
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native long H5Sget_select_npoints(int space_id)
		throws HDF5LibraryException;

	/**
	 *  H5Sget_simple_extent_ndims determines the dimensionality 
	 *  (or rank) of a dataspace.
	 *
	 *  @param space_id  Identifier of the dataspace
	 *  @return the number of dimensions in the dataspace if 
	 *  successful
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native int H5Sget_simple_extent_ndims(int space_id)
		throws HDF5LibraryException;


	/**
	 *  H5Sget_simple_extent_dims returns the size and maximum sizes of
	 *  each dimension of a dataspace through the dims and maxdims
	 *  parameters.
	 *
	 *  @param space_id  IN: Identifier of the dataspace object to 
	 *  query
	 *  @param dims  OUT: Pointer to array to store the size of 
	 *  each dimension.
	 *  @param maxdims  OUT: Pointer to array to store the maximum 
	 *  size of each dimension.
	 *
	 *  @return the number of dimensions in the dataspace if 
	 *  successful
	 * 
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 *  @exception NullPointerException - dims or maxdims is null.
	 **/
	public static native int H5Sget_simple_extent_dims(int space_id, 
		long[] dims, long[] maxdims) 
		throws HDF5LibraryException, 
		NullPointerException;


	/**
	 *  H5Sget_simple_extent_type queries a dataspace to determine the
	 *  current class of a dataspace.
	 *
	 *  @param space_id  Dataspace identifier.
	 *
	 *  @return a dataspace class name if successful
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native int H5Sget_simple_extent_type(int space_id)
		throws HDF5LibraryException;


	/**
	 *  H5Sset_extent_simple sets or resets the size of an existing 
	 *  dataspace.
	 *
	 *  @param space_id Dataspace identifier.
	 *  @param rank Rank, or dimensionality, of the dataspace.
	 *  @param current_size Array containing current size of dataspace.
	 *  @param maximum_size Array containing maximum size of dataspace.
	 *
	 *  @return a dataspace identifier if successful
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native int H5Sset_extent_simple(int space_id, 
		int rank, long[] current_size, long[] maximum_size)
		throws HDF5LibraryException, 
		NullPointerException;

	/**
	 *  H5Sis_simple determines whether a dataspace is a simple 
	 *  dataspace.
	 *
	 *  @param space_id  Identifier of the dataspace to query
	 *
	 *  @return true if is a simple dataspace
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native boolean H5Sis_simple(int space_id)
		throws HDF5LibraryException;


	/**
	 *  H5Soffset_simple sets the offset of a simple dataspace 
	 *  space_id.
	 *
	 *  @param space_id  IN: The identifier for the dataspace 
	 *  object to reset.
	 *  @param offset  IN: The offset at which to position the 
	 *  selection.
	 *
	 *  @return a non-negative value if successful
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 *  @exception NullPointerException - offset array is null.
	 **/
	public static native int H5Soffset_simple(int space_id, 
		long[] offset)
		throws HDF5LibraryException, 
		NullPointerException;

	/**
	 *  H5Sextent_copy copies the extent from source_space_id to
	 *  dest_space_id. This action may change the type of the 
	 *  dataspace.
	 *
	 *  @param dest_space_id  IN: The identifier for the dataspace 
	 *  from which the extent is copied.
	 *  @param source_space_id  IN: The identifier for the 
	 *  dataspace to which the extent is copied.
	 *
	 *  @return a non-negative value if successful
	 * 
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native int H5Sextent_copy(int dest_space_id, 
		int source_space_id)
		throws HDF5LibraryException;

	/**
	 *  H5Sset_extent_none removes the extent from a dataspace and 
	 *  sets the type to H5S_NONE.
	 *
	 *  @param space_id The identifier for the dataspace from 
	 *  which the extent is to be removed.
	 *
	 *  @return a non-negative value if successful
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native int H5Sset_extent_none(int space_id) 
		throws HDF5LibraryException;

	/**
	 *  H5Sselect_hyperslab selects a hyperslab region to add to 
	 *  the current selected region for the dataspace specified 
	 *  by space_id.  The start, stride, count, and block arrays 
	 *  must be the same size as the rank of the dataspace.
	 *
	 *  @param space_id  IN: Identifier of dataspace selection 
	 *  to modify
	 *  @param op  IN: Operation to perform on current selection.
	 *  @param start  IN: Offset of start of hyperslab
	 *  @param count  IN: Number of blocks included in hyperslab.
	 *  @param stride  IN: Hyperslab stride.
	 *  @param block  IN: Size of block in hyperslab.
	 *
	 *  @return a non-negative value if successful
	 * 
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 *  @exception NullPointerException - an input array is null.
	 *  @exception NullPointerException - an input array is invalid.
	 **/
	public static native int H5Sselect_hyperslab(int space_id, 
		int op, long[] start,
		long[] stride, long[] count, long[] block)
		throws HDF5LibraryException, 
		NullPointerException, 
		IllegalArgumentException;

	/**
	 *  H5Sclose releases a dataspace.
	 *
	 *  @param space_id  Identifier of dataspace to release.
	 *
	 *  @return a non-negative value if successful
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native int H5Sclose(int space_id) 
		throws HDF5LibraryException;

//--//

	// following static native functions are missing from HDF5 (version 1.0.1) RM

	/**
	 *  H5Sget_select_hyper_nblocks returns the number of 
	 *  hyperslab blocks in the current dataspace selection. 
	 *
	 *  @param spaceid  Identifier of dataspace to release.
	 *
	 *  @return a non-negative value if successful
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native long H5Sget_select_hyper_nblocks(int spaceid)
		throws HDF5LibraryException;

	/**
	 *  H5Sget_select_elem_npoints returns the number of 
	 *  element points in the current dataspace selection. 
	 *
	 *  @param spaceid  Identifier of dataspace to release.
	 *
	 *  @return a non-negative value if successful
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native long H5Sget_select_elem_npoints(int spaceid)
		throws HDF5LibraryException;

	/**
	 *  H5Sget_select_hyper_blocklist returns an array of
	 *  hyperslab blocks. The block coordinates have the 
	 *  same dimensionality (rank) as the dataspace they 
	 *  are located within. The list of blocks is formatted 
	 *  as follows: 
	 *  <pre>
         *    <"start" coordinate>, immediately followed by 
         *    <"opposite" corner coordinate>, followed by 
         *   the next "start" and "opposite" coordinates, 
         *   etc. 
    	 *   until all of the selected blocks have been listed. 
	 * </pre>
	 *
	 *  @param spaceid  Identifier of dataspace to release.
	 *  @param startblock  first block to retrieve
	 *  @param numblock  number of blocks to retrieve
	 *  @param buf  returns blocks startblock to startblock+num-1,
	 *  each block is <i>rank</i> * 2 (corners) longs.
	 *
	 *  @return a non-negative value if successful
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 *  @exception NullPointerException - buf is null.
	 **/
	public static native int H5Sget_select_hyper_blocklist(int spaceid,
		long startblock, long numblocks, long[] buf)
		throws HDF5LibraryException, 
		NullPointerException;

	/**
	 *  H5Sget_select_elem_pointlist returns an array of
	 *  of element points in the current dataspace selection. 
	 *  The point coordinates have the same dimensionality 
	 *  (rank) as the dataspace they are located within,
	 *  one coordinate per point.
	 *
	 *  @param spaceid  Identifier of dataspace to release.
	 *  @param startpoint  first point to retrieve
	 *  @param numpoints  number of points to retrieve
	 *  @param buf  returns points startblock to startblock+num-1,
	 *  each points is <i>rank</i> longs.
	 *
	 *  @return a non-negative value if successful
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 *  @exception NullPointerException - buf is null.
	 **/
	public static native int H5Sget_select_elem_pointlist(int spaceid, 
		long startpoint, long numpoints, long[] buf)
		throws HDF5LibraryException, 
		NullPointerException;

	/**
	 *  H5Sget_select_bounds retrieves the coordinates of 
	 *  the bounding box containing the current
         *  selection and places them into user-supplied buffers. 
	 *  <P>
	 *  The start and end buffers must be large enough to 
	 *  hold the dataspace rank number of coordinates. 
	 *
	 *  @param spaceid  Identifier of dataspace to release.
	 *  @param start  coordinates of lowest corner of bounding box.
	 *  @param end  coordinates of highest corner of bounding box.
	 *
	 *  @return a non-negative value if successful,with start and
	 *  end initialized.
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 *  @exception NullPointerException - start or end is null.
	 **/
	public static native int H5Sget_select_bounds(int spaceid, 
		long[] start, long[] end)
		throws HDF5LibraryException, 
		NullPointerException;


	//////////////////////////////////////////////////////////////
	//                                                          //
	//           H5T: Datatype Interface Functions              //
	//                                                          //
	//////////////////////////////////////////////////////////////

	/**
	 *  H5Topen opens a named datatype at the location specified 
	 *  by loc_id and return an identifier for the datatype.
	 *
	 *  @param loc_id A file, group, or datatype identifier.
	 *  @param name  A datatype name.
	 *
	 *  @return a named datatype identifier if successful
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 *  @exception NullPointerException - name is null.
	 **/
	public static native int H5Topen(int loc_id, String name)
		throws HDF5LibraryException, NullPointerException;

	/**
	 *  H5Tcommit commits a transient datatype (not immutable) 
	 *  to a file, turned it into a named datatype.
	 *
	 *  @param loc_id A file or group identifier.
	 *  @param name A datatype name.
	 *  @param type A datatype identifier.
	 *
	 *  @return a non-negative value if successful
	 * 
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 *  @exception NullPointerException - name is null.
	 **/
	public static native int H5Tcommit(int loc_id, String name, 
		int type)
		throws HDF5LibraryException, NullPointerException;

	/**
	 *  H5Tcommitted queries a type to determine whether the type 
	 *  specified by the type identifier is a named type or a 
	 *  transient type.
	 *
	 *  @param type Datatype identifier.
	 *
	 *  @return true if successfully committed
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native boolean H5Tcommitted(int type) 
		throws HDF5LibraryException;

	/**
	 *  H5Tcreate creates a new dataype of the specified class with 
	 *  the specified number of bytes.
	 *
	 *  @param dclass Class of datatype to create.
	 *  @param size The number of bytes in the datatype to create.
	 *
	 *  @return datatype identifier if successful
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native int H5Tcreate(int dclass, int size) 
		throws HDF5LibraryException;

	/**
	 *  H5Tcopy copies an existing datatype. The returned type is 
	 *  always transient and unlocked.
	 *
	 *  @param type_id  Identifier of datatype to copy. Can 
	 *  be a datatype identifier, a  predefined datatype 
	 *  (defined in H5Tpublic.h), or a dataset Identifier.
	 *
	 *  @return a datatype identifier if successful
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native int H5Tcopy(int type_id) 
		throws HDF5LibraryException;


	/**
	 *  H5Tequal determines whether two datatype identifiers refer 
	 *  to the same datatype.
	 *
	 *  @param type_id1  Identifier of datatype to compare.
	 *  @param type_id2  Identifier of datatype to compare.
	 *
	 *  @return true if the datatype identifiers refer to the 
	 *  same datatype, else FALSE.
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native boolean H5Tequal(int type_id1, int type_id2)
		throws HDF5LibraryException;

	/**
	 *  H5Tlock locks the datatype specified by the type_id 
	 *  identifier, making it read-only and non-destrucible.
	 *
	 *  @param type_id  Identifier of datatype to lock.
	 *
	 *  @return a non-negative value if successful
	 *
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native int H5Tlock(int type_id) 
		throws HDF5LibraryException;

	/**
	 *  H5Tget_class returns the datatype class identifier.
	 *
	 *  @param type_id  Identifier of datatype to query.
	 *
	 *  @return datatype class identifier if successful; 
	 *  otherwise H5T_NO_CLASS (-1).
	 * 
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native int H5Tget_class(int type_id) 
		throws HDF5LibraryException;

	/**
	 *  H5Tget_size returns the size of a datatype in bytes.
	 *
	 *  @param type_id  Identifier of datatype to query.
	 *
	 *  @return the size of the datatype in bytes if successful
	 * 
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native int H5Tget_size(int type_id) 
		throws HDF5LibraryException;

	/**
	 *  H5Tset_size sets the total size in bytes, size, for an 
	 *  atomic datatype (this operation is not permitted on 
	 *  compound datatypes).
	 *
	 *  @param type_id  Identifier of datatype to change size.
	 *  @param size Size in bytes to modify datatype.
	 *
	 *  @return a non-negative value if successful
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native int H5Tset_size(int type_id, int size) 
		throws HDF5LibraryException;

	/**
	 *  H5Tget_order returns the byte order of an atomic datatype.
	 *
	 *  @param type_id  Identifier of datatype to query.
	 *
	 *  @return a byte order constant if successful
	 * 
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native int H5Tget_order(int type_id) 
		throws HDF5LibraryException;

	/**
	 *  H5Tset_order sets the byte ordering of an atomic datatype.
	 *
	 *  @param type_id  Identifier of datatype to set.
	 *  @param order Byte ordering constant.
	 *
	 *  @return a non-negative value if successful
	 * 
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native int H5Tset_order(int type_id, int order)
		throws HDF5LibraryException;

	/**
	 *  H5Tget_precision returns the precision of an atomic datatype.
	 *
	 *  @param type_id  Identifier of datatype to query.
	 *
	 *  @return the number of significant bits if successful
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native int H5Tget_precision(int type_id) 
		throws HDF5LibraryException;

	/**
	 *  H5Tset_precision sets the precision of an atomic datatype.
	 *
	 *  @param type_id  Identifier of datatype to set.
	 *  @param precision  Number of bits of precision for datatype.
	 *
	 *  @return a non-negative value if successful
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native int H5Tset_precision(int type_id, 
		int precision)
		throws HDF5LibraryException;

	/**
	 *  H5Tget_offset retrieves the bit offset of the first 
	 *  significant bit.
	 *
	 *  @param type_id  Identifier of datatype to query.
	 *  @return a positive offset value if successful; otherwise 0.
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native int H5Tget_offset(int type_id) 
		throws HDF5LibraryException;


	/**
	 *  H5Tset_offset sets the bit offset of the first 
	 *  significant bit.
	 *
	 *  @param type_id  Identifier of datatype to set.
	 *  @param offset  Offset of first significant bit.
	 *
	 *  @return a non-negative value if successful
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native int H5Tset_offset(int type_id, int offset)
		throws HDF5LibraryException;

	/**
	 *  H5Tget_pad retrieves the padding type of the least and 
	 *  most-significant bit padding.
	 *
	 *  @param type_id  IN: Identifier of datatype to query.
	 *  @param pad  OUT: locations to return least-significant 
	 *  and most-significant bit padding type.
	 *  <pre>
	 *      pad[0] = lsb // least-significant bit padding type
	 *      pad[1] = msb // most-significant bit padding type
	 *  </pre>
	 *
	 *  @return a non-negative value if successful
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 *  @exception NullPointerException - pad is null.
	 **/
	public static native int H5Tget_pad(int type_id, int[] pad)
		throws HDF5LibraryException, NullPointerException;

	/**
	 *  H5Tset_pad sets the least and most-significant bits 
	 *  padding types.
	 *
	 *  @param type_id  Identifier of datatype to set.
	 *  @param lsb Padding type for least-significant bits.
	 *  @param msb Padding type for most-significant bits.
	 *
	 *  @return a non-negative value if successful
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native int H5Tset_pad(int type_id, int lsb, int msb)
		throws HDF5LibraryException;

	/**
	 *  H5Tget_sign retrieves the sign type for an integer type.
	 *
	 *  @param type_id  Identifier of datatype to query.
	 *
	 *  @return a valid sign type if successful
	 * 
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native int H5Tget_sign(int type_id) 
		throws HDF5LibraryException;

	/**
	 *  H5Tset_sign sets the sign proprety for an integer type.
	 *
	 *  @param type_id  Identifier of datatype to set.
	 *  @param sign Sign type.
	 *
	 *  @return a non-negative value if successful
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native int H5Tset_sign(int type_id, int sign)
		throws HDF5LibraryException;

	/**
	 *  H5Tget_fields retrieves information about the locations of 
	 *  the various bit fields of a floating point datatype.
	 *
	 *  @param type_id  IN: Identifier of datatype to query.
	 *  @param fields  OUT: location of size and bit-position.
	 *  <pre>
	 *      fields[0] = spos  OUT: location to return size of in bits.
	 *      fields[1] = epos  OUT: location to return exponent 
	 *                  bit-position.
	 *      fields[2] = esize  OUT: location to return size of 
	 *                  exponent in bits.
	 *      fields[3] = mpos  OUT: location to return mantissa 
	 *                  bit-position.
	 *      fields[4] = msize  OUT: location to return size of 
	 *                  mantissa in bits.
	 *  </pre>
	 *
	 *  @return a non-negative value if successful
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 *  @exception NullPointerException - fileds is null.
	 *  @exception IllegalArgumentException - fileds array is invalid.
	 **/
	public static native int H5Tget_fields(int type_id, int[] fields) 
		throws HDF5LibraryException, 
		NullPointerException, 
		IllegalArgumentException;


	/**
	 *  H5Tset_fields sets the locations and sizes of the various
	 *  floating point bit fields.
	 *
	 *  @param type_id  Identifier of datatype to set.
	 *  @param spos Size position.
	 *  @param epos Exponent bit position.
	 *  @param esize Size of exponent in bits.
	 *  @param mpos Mantissa bit position.
	 *  @param msize Size of mantissa in bits.
	 *
	 *  @return a non-negative value if successful
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native int H5Tset_fields(int type_id, int spos, 
		int epos, int esize,
		int mpos, int msize) throws HDF5LibraryException;

	/**
	 *  H5Tget_ebias retrieves the exponent bias of a 
	 *  floating-point type.
	 *
	 *  @param type_id  Identifier of datatype to query.
	 *
	 *  @return the bias if successful; otherwise 0.
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native int H5Tget_ebias(int type_id) 
		throws HDF5LibraryException;

	/**
	 *  H5Tset_ebias sets the exponent bias of a floating-point type.
	 *
	 *  @param type_id  Identifier of datatype to set.
	 *  @param ebias Exponent bias value.
	 *
	 *  @return a non-negative value if successful
	 * 
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native int H5Tset_ebias(int type_id, int ebias)
		throws HDF5LibraryException;

	/**
	 *  H5Tget_norm retrieves the mantissa normalization of a 
	 *  floating-point datatype.
	 *
	 *  @param type_id  Identifier of datatype to query.
	 *
	 *  @return a valid normalization type if successful
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native int H5Tget_norm(int type_id) 
		throws HDF5LibraryException;

	/**
	 *  H5Tset_norm sets the mantissa normalization of a 
	 *  floating-point datatype.
	 *
	 *  @param type_id  Identifier of datatype to set.
	 *  @param norm Mantissa normalization type.
	 *
	 *  @return a non-negative value if successful
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native int H5Tset_norm(int type_id, int norm) 
		throws HDF5LibraryException;

	/**
	 *  H5Tget_inpad retrieves the internal padding type for unused 
	 *  bits in floating-point datatypes.
	 *
	 *  @param type_id  Identifier of datatype to query.
	 *
	 *  @return a valid padding type if successful
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native int H5Tget_inpad(int type_id)
		throws HDF5LibraryException;


	/**
	 *  If any internal bits of a floating point type are unused 
	 *  (that is, those significant bits which are not part of 
	 *  the sign, exponent, or mantissa), then  H5Tset_inpad will 
	 *  be filled according to the value of the padding value
	 *  property inpad.
	 *
	 *  @param type_id  Identifier of datatype to modify.
	 *  @param inpad Padding type.
	 *
	 *  @return a non-negative value if successful
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native int H5Tset_inpad(int type_id, int inpad)
		throws HDF5LibraryException;

	/**
	 *  H5Tget_cset retrieves the character set type of a 
	 *  string datatype.
	 *
	 *  @param type_id  Identifier of datatype to query.
	 *
	 *  @return a valid character set type if successful
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native int H5Tget_cset(int type_id)
		throws HDF5LibraryException;

	/**
	 *  H5Tset_cset the character set to be used.
	 *
	 *  @param type_id  Identifier of datatype to modify.
	 *  @param cset Character set type.
	 *
	 *  @return a non-negative value if successful
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native int H5Tset_cset(int type_id, int cset)
		throws HDF5LibraryException;


	/**
	 *  H5Tget_strpad retrieves the string padding method for 
	 *  a string datatype.
	 *
	 *  @param type_id  Identifier of datatype to query.
	 *
	 *  @return a valid string padding type if successful
	 * 
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native int H5Tget_strpad(int type_id)
		throws HDF5LibraryException;

	/**
	 *  H5Tset_strpad defines the storage mechanism for the string.
	 *
	 *  @param type_id Identifier of datatype to modify.
	 *  @param strpad String padding type.
	 *
	 *  @return a non-negative value if successful
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native int H5Tset_strpad(int type_id, int strpad)
		throws HDF5LibraryException;

	/**
	 *  H5Tget_nmembers retrieves the number of fields a 
	 *  compound datatype has.
	 *
	 *  @param type_id  Identifier of datatype to query.
	 *
	 *  @return number of members datatype has if successful
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native int H5Tget_nmembers(int type_id)
		throws HDF5LibraryException;

	/**
	 *  H5Tget_member_name retrieves the name of a field of a compound
	 *  datatype.
	 *
	 *  @param type_id  Identifier of datatype to query.
	 *  @param field_idx Field index (0-based) of the field name 
	 *  to retrieve.
	 *
	 *  @return a valid pointer if successful; otherwise null.
	 *
	 **/
	public static native String H5Tget_member_name(int type_id, 
		int field_idx);

	/**
	 *  H5Tget_member_dims returns the dimensionality of the field.
	 *
	 *  @param type_id  Identifier of datatype to query.
	 *  @param field_idx  Field index (0-based) of the field dims 
	 *  to retrieve.
	 *  @param dims Pointer to buffer to store the dimensions of 
	 *  the field.
	 *  @param perm Pointer to buffer to store the permutation 
	 *  vector of the field.
	 *
	 *  @return the number of dimensions, a number from 0 to 4, 
	 *  if successful.
	 *  Otherwise returns a negative value.
	 * 
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native int H5Tget_member_dims(int type_id, 
		int field_idx, int[] dims, int[] perm) 
		throws HDF5LibraryException, 
		NullPointerException;

	/**
	 *  H5Tget_member_type returns the datatype of the specified 
	 *  member.
	 *
	 *  @param type_id  Identifier of datatype to query.
	 *  @param field_idx Field index (0-based) of the field type to 
	 *  retrieve.
	 *
	 *  @return the identifier of a copy of the datatype of the 
	 *  field if successful;
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native int H5Tget_member_type(int type_id, 
		int field_idx)
		throws HDF5LibraryException;

	/**
	 *  H5Tget_member_offset returns the byte offset of the 
	 *  specified member of the compound datatype.  This
	 *  is the byte offset in the HDF-5 file/library, NOT
	 *  the offset of any Java object which might be mapped
	 *  to this data item.
	 *
	 *  @param type_id  Identifier of datatype to query.
	 *  @param field_idx Field index (0-based) of the field type to 
	 *  retrieve.
	 *
	 *  @return the offset of the member.
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native long H5Tget_member_offset(int type_id, 
		int membno)
		throws HDF5LibraryException;

	/**
	 *  H5Tinsert adds another member to the compound datatype type_id.
	 *
	 *  @param type_id  Identifier of compound datatype to modify.
	 *  @param name  Name of the field to insert.
	 *  @param offset Offset in memory structure of the field to 
	 *  insert.
	 *  @param field_id  Datatype identifier of the field to insert.
	 *
	 *  @return a non-negative value if successful
	 *  
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 *  @exception NullPointerException - name is null.
	 **/
	public static native int H5Tinsert(int type_id, String name, 
		long offset, int field_id)
		throws HDF5LibraryException, NullPointerException;


	/**
	 *  H5Tpack recursively removes padding from within a 
	 *  compound datatype to make it more efficient (space-wise) 
	 *  to store that data.  
	 *  <P>
	 *  <b>WARNING:</b> This call only affects the
	 *  C-data, even if it succeeds, there may be no visible 
	 *  effect on Java objects.
	 *
	 *  @param type_id  Identifier of datatype to modify.
	 *
	 *  @return a non-negative value if successful
	 * 
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native int H5Tpack(int type_id) 
		throws HDF5LibraryException;


 	/**
	 *  H5Tinsert_array adds a new member to the compound datatype
	 *  parent_id.
	 *
	 *  @param parent_id  Identifier of the parent compound datatype.
	 *  @param name Name of new member.
	 *  @param offset Offset to start of new member within compound 
	 *  datatype.
	 *  @param ndims Dimensionality of new member.
	 *  @param dim Size of new member array.
	 *  @param perm Buffer to store the permutation vector of 
	 *  the field.
	 *  @param member_id  Identifier of the datatype of the new member.
	 *
	 *  @return a non-negative value if successful
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 *  @exception NullPointerException - name is null.
	 **/
	public static native int H5Tinsert_array(int parent_id, 
		String name, int offset,
		int ndims, int[] dim, int[] perm, int member_id)
		throws HDF5LibraryException, NullPointerException;


	/**
	 *  H5Tclose releases a datatype.
	 *
	 *  @param type_id  Identifier of datatype to release.
	 *
	 *  @return a non-negative value if successful
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native int H5Tclose(int type_id) 
		throws HDF5LibraryException;

	// following static native functions are missing from HDF5 (version 1.0.1) RM

	/**
	 *  H5Tenum_create creates a new enumeration datatype 
	 *  based on the specified base datatype, parent_id, 
	 *  which must be an integer type. 
	 *
	 *  @param base_id  Identifier of the parent datatype to release.
	 *
	 *  @return a non-negative value if successful
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native int H5Tenum_create(int base_id) 
		throws HDF5LibraryException;

	/**
	 *  H5Tenum_insert inserts a new enumeration datatype member 
	 *  into an enumeration datatype. 
	 *
	 *  @param type  Identifier of datatype.
	 *  @param name  The name of the member
	 *  @param obj  The value of the member,  data of the correct
	 *  type
	 *
	 *  @return a non-negative value if successful
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 *  @exception NullPointerException - name is null.
	 **/
	public static native int H5Tenum_insert(int type, String name, 
		int[] value)
		throws HDF5LibraryException, NullPointerException;

	/**
	 *  H5Tenum_nameof finds the symbol name that corresponds 
	 *  to the specified value of the enumeration datatype type. 
	 *
	 *  @param type  IN: Identifier of datatype.
	 *  @param obj  IN: The value of the member, data of the correct
	 *  @param name  OUT: The name of the member
	 *  @param size  IN:  The max length of the name
	 *
	 *  @return a non-negative value if successful
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 *  @exception NullPointerException - name is null.
	 **/
	public static native int H5Tenum_nameof(int type, 
		int[] value, String[] name, int size)
		throws HDF5LibraryException, NullPointerException;


	/**
	 *  H5Tenum_valueof finds the value that corresponds to 
	 *  the specified name of the enumeration datatype type. 
	 *
	 *  @param type  IN: Identifier of datatype.
	 *  @param name  IN: The name of the member
	 *  @param value  OUT: The value of the member
	 *
	 *  @return a non-negative value if successful
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 *  @exception NullPointerException - name is null.
	 **/
	public static native int H5Tenum_valueof(int type, 
		String name, int[] value)
		throws HDF5LibraryException, NullPointerException;

	/**
	 *  H5Tvlen_create creates a new variable-length (VL) dataype. 
	 *
	 *  @param base_id_type  IN: Identifier of parent datatype.
	 *
	 *  @return a non-negative value if successful
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native int H5Tvlen_create(int base_id)
		throws HDF5LibraryException;

	/**
	 *  H5Tset_tag tags an opaque datatype type_id with a 
	 *  unique ASCII identifier tag. 
	 *
	 *  @param type  IN: Identifier of parent datatype.
	 *  @param tag  IN: Name of the tag (will be stored as
	 *  ASCII)
	 *
	 *  @return a non-negative value if successful
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native int H5Tset_tag(int type, String tag)
		throws HDF5LibraryException;

	/**
	 *  H5Tget_tag returns the tag associated with datatype type_id.
	 *
	 *  @param type  IN: Identifier of datatype.
	 *
	 *  @return the tag
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native String H5Tget_tag(int type) 
		throws HDF5LibraryException;

	/**
	 *  H5Tget_super returns the type from which TYPE is derived.
	 *
	 *  @param type  IN: Identifier of datatype.
	 *
	 *  @return the parent type
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 **/
	public static native int H5Tget_super(int type) 
		throws HDF5LibraryException;

	/**
	 *  H5Tget_member_value returns the value of the enumeration 
	 *  datatype member memb_no. 
	 *
	 *  @param type_id  IN: Identifier of datatype.
	 *  @param membno  IN: The name of the member
	 *  @param value  OUT: The value of the member
	 *
	 *  @return a non-negative value if successful
	 *
	 *  @exception HDF5LibraryException - Error from the HDF-5 Library.
	 *  @exception NullPointerException - name is null.
	 **/
	public static native int H5Tget_member_value(int type_id, 
		int membno, int[] value)
		throws HDF5LibraryException, NullPointerException;

}
