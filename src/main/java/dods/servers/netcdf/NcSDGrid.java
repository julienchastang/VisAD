// $Id: NcSDGrid.java,v 1.3 2004-02-06 15:23:49 donm Exp $
/*
 * Copyright 1997-2000 Unidata Program Center/University Corporation for
 * Atmospheric Research, P.O. Box 3000, Boulder, CO 80307,
 * support@unidata.ucar.edu.
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or (at
 * your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser
 * General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation,
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package dods.servers.netcdf;

import ucar.ma2.*;
import ucar.nc2.*;

import dods.dap.Server.*;
import dods.dap.*;

import java.io.IOException;
import java.io.EOFException;
import java.util.ArrayList;

/**
 * Wraps a netcdf variable with rank > 0, whose dimensions all
 * have coordinate variables, as an SDGrid.
 *
 * @version $Revision: 1.3 $
 * @author jcaron
 */

public class NcSDGrid extends SDGrid {

  /** Constructor.
   *  @param v the netcdf variable
   *  @param arr the variable wrapped in an NcSDArray.
   */
  public NcSDGrid( String name, ArrayList list) {
    super(name);

    addVariable( (BaseType) list.get(0), ARRAY);

    for (int i=1; i<list.size(); i++)
      addVariable( (BaseType) list.get(i), MAPS);
  }

  public boolean read(String datasetName, Object specialO) throws NoSuchVariableException,
    IOException, EOFException {

    java.util.Enumeration vars = getVariables();
    while (vars.hasMoreElements()) {
      SDArray bt = (SDArray) vars.nextElement();
      bt.read( datasetName, specialO);
    }

    setRead(true);
    return(false);
  }
}

/* Change History:
   $Log: not supported by cvs2svn $
   Revision 1.1.1.1  2001/09/26 15:34:30  caron
   checkin beta1


 */
