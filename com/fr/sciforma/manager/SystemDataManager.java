/*
 * © 2008 Sciforma. Tous droits réservés. 
 */
package com.fr.sciforma.manager;

import com.sciforma.psnext.api.FieldDefinitions;
import com.sciforma.psnext.api.InterLinkMap;
import com.sciforma.psnext.api.SystemCalendarList;
import com.sciforma.psnext.api.SystemTableLookupList;

import com.fr.sciforma.exception.BusinessException;

public interface SystemDataManager {

	public SystemCalendarList getSystemCalendarList() throws BusinessException;

	public FieldDefinitions getFieldDefinitions() throws BusinessException;

	public SystemTableLookupList getTableLookupDefinition()
			throws BusinessException;

	public InterLinkMap getInterLinkMap() throws BusinessException;
}
