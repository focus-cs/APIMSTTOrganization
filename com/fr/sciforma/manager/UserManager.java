/*
 * © 2008 Sciforma. Tous droits réservés. 
 */
package com.fr.sciforma.manager;

import java.util.List;

import com.sciforma.psnext.api.PSException;
import com.sciforma.psnext.api.User;

import com.fr.sciforma.exception.BusinessException;
import com.fr.sciforma.exception.TechnicalException;

/**
 * manage l'accès aux users
 */
public interface UserManager {

    /**
     * find a resource by this ID
     *
     * @param id resource ID
     * @return PSNExt resource
     * @throws BusinessException
     * @throws TechnicalException
     */
    User findUserById(String id) throws TechnicalException;

    /**
     * filtre pour les utilisateurs
     */
    public interface UserFilter {

        boolean filter(User user) throws PSException;
    }

    /**
     * find a resource by Criteria
     *
     * @param id resource ID
     * @return PSNExt resource
     * @throws BusinessException
     * @throws TechnicalException
     */
    List<User> findUserByCriteria(UserFilter userFilter)
            throws TechnicalException;
}
