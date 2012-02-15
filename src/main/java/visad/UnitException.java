//
// UnitException.java
//

/*
VisAD system for interactive analysis and visualization of numerical
data.  Copyright (C) 1996 - 2011 Bill Hibbard, Curtis Rueden, Tom
Rink, Dave Glowacki, Steve Emmerson, Tom Whittaker, Don Murray, and
Tommy Jasmin.

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Library General Public
License as published by the Free Software Foundation; either
version 2 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Library General Public License for more details.

You should have received a copy of the GNU Library General Public
License along with this library; if not, write to the Free
Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
MA 02111-1307, USA
*/

package visad;

/**
 * A class for exceptions in the units package.
 * @author Steve Emmerson
 *
 * This is part of Steve Emmerson's Unit package that has been
 * incorporated into VisAD.
 */
public class UnitException
    extends VisADException    // change by Bill Hibbard for VisAD
{
    /**
     * Create an exception with no detail message.
     */
    public UnitException()
    {
	super();
    }

    /**
     * Create an exception with a detail message.
     */
    public UnitException(String msg)
    {
	super(msg);
    }
}
