/*
 * Copyright 2014-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.lastaflute.jta.core;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.InvalidTransactionException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.TransactionSynchronizationRegistry;
import javax.transaction.UserTransaction;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class RestrictedTransactionManagerImpl implements TransactionManager {

    protected UserTransaction userTransaction;

    protected TransactionSynchronizationRegistry synchronizationRegistry;

    public RestrictedTransactionManagerImpl() {
    }

    public RestrictedTransactionManagerImpl(final UserTransaction userTransaction,
            final TransactionSynchronizationRegistry synchronizationRegistry) {
        this.userTransaction = userTransaction;
        this.synchronizationRegistry = synchronizationRegistry;
    }

    public void setUserTransaction(final UserTransaction userTransaction) {
        this.userTransaction = userTransaction;
    }

    public void setSynchronizationRegistry(final TransactionSynchronizationRegistry synchronizationRegistry) {
        this.synchronizationRegistry = synchronizationRegistry;
    }

    public void begin() throws NotSupportedException, SystemException {
        userTransaction.begin();
    }

    public void commit() throws HeuristicMixedException, HeuristicRollbackException, IllegalStateException, RollbackException,
            SecurityException, SystemException {
        userTransaction.commit();
    }

    public int getStatus() throws SystemException {
        return userTransaction.getStatus();
    }

    public Transaction getTransaction() throws SystemException {
        final int status = getStatus();
        if (status == Status.STATUS_NO_TRANSACTION || status == Status.STATUS_UNKNOWN) {
            return null;
        }
        RestrictedTransactionImpl tx = (RestrictedTransactionImpl) synchronizationRegistry.getResource(this);
        if (tx == null) {
            tx = new RestrictedTransactionImpl(userTransaction, synchronizationRegistry);
            synchronizationRegistry.putResource(this, tx);
        }
        return tx;
    }

    public void resume(final Transaction tx) throws IllegalStateException, InvalidTransactionException, SystemException {
        throw new UnsupportedOperationException("resume");
    }

    public void rollback() throws IllegalStateException, SecurityException, SystemException {
        userTransaction.rollback();
    }

    public void setRollbackOnly() throws IllegalStateException, SystemException {
        userTransaction.setRollbackOnly();
    }

    public void setTransactionTimeout(final int seconds) throws SystemException {
        userTransaction.setTransactionTimeout(seconds);
    }

    public Transaction suspend() throws SystemException {
        throw new UnsupportedOperationException("suspend");
    }

}
