/*
 * Copyright (c) 2015, Kasra Faghihi, All rights reserved.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.
 */
package com.offbynull.coroutines.user;

/**
 * Used to execute a {@link Coroutine}. All {@link Coroutine}s must be executed through this class.
 * @author Kasra Faghihi
 */
public final class CoroutineRunner {
    private Coroutine coroutine;
    private Continuation continuation = new Continuation();

    /**
     * Constructs a {@link CoroutineRunner} object.
     * @param coroutine coroutine to run
     * @throws NullPointerException if any argument is {@code null}
     */
    public CoroutineRunner(Coroutine coroutine) {
        if (coroutine == null) {
            throw new NullPointerException();
        }
        this.coroutine = coroutine;
    }

    /**
     * Starts/resumes executes this coroutine.
     * <p>
     * If this method returns {@code true} and you call it again, it'll start executing this coroutine from the beginning. If this method
     * returns {@code false} and you call it again, it'll resume executing this coroutine from the point which it suspended.
     * @return {@code true} if execution completed, {@code false} if execution was suspended.
     * @throws CoroutineException an exception occurred during execution of this coroutine, the saved execution stack and object state may
     * be out of sync at this point (meaning that unless you know what you're doing, you should not use this coroutine object again)
     */
    public boolean execute() {
        try {
            coroutine.run(continuation);
            continuation.finishedExecutionCycle();
        } catch (Exception e) {
            throw new CoroutineException("Exception thrown during execution", e);
        }
        
        // if mode was not set to SAVING after return, it means the method finished executing
        if (continuation.getMode() != Continuation.MODE_SAVING) {
            continuation.reset(); // clear methodstates + set to normal, this is not explicitly nessecary at this point but set anyways
            return false;
        } else {
            continuation.setMode(Continuation.MODE_LOADING); // set to loading for next invokation
            return true;
        }
    }
}