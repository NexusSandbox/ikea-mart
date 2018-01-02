package eli.ikea.mart.formatter;

import java.text.MessageFormat;

/**
 * A generic builder pattern used to configure complex POJOs.
 * <p style="font-style:italic;font-variant;small-caps;font-size:80%">
 * Copyright &copy; Cerner Corporation 2017 All rights reserved.
 * </p>
 *
 * @author Elisha Boozer (EB019254)
 * @author Millennium Platform - Messaging
 * @param <CRITERIA> Type of hosting object.
 * @since X.X
 */
public abstract class AbstractTestingBuilder<CRITERIA>
{
    /**
     * criteria to configure. As a generic variable to hold the builders POJO object.
     */
    protected final CRITERIA criteria;
    private boolean          finalized = false;

    /**
     * @param criteria The object currently being built.
     */
    protected AbstractTestingBuilder(final CRITERIA criteria)
    {
        this.criteria = criteria;
    }

    /**
     * Perform any additional finalization steps. This will only be called when {@link #finish()} is called. By default, this method does nothing, but
     * may be overridden to handle additional finishing tasks that can only be completed after the builder's criteria has been set.
     */
    protected void build()
    {
        // Intentionally blank, but intended to be overridden for any additional finalization steps.
    }

    /**
     * Finalizes the builder object and returns the configured criteria.
     *
     * @return the finalized non-null configured criteria.
     * @throws com.cerner.system.exception.VerifyException if the builder's criteria is not currently in a valid state.
     */
    public CRITERIA finish()
    {
        if (!finalized)
        {
            validate();
            build();
            finalized = true;
        }

        return criteria;
    }

    /**
     * @return <code>True</code> if the builder has already been finalized, otherwise <code>false</code>.
     */
    public boolean isFinalized()
    {
        return finalized;
    }

    /**
     * Ensures the builder's criteria are in a valid state. This will only be called when {@link #finish()} is called.
     *
     * @throws com.cerner.system.exception.VerifyException if the builder's criteria is not currently in a valid state.
     */
    protected abstract void validate();

    /**
     * Verifies that the builder is not in a finalized state. Only the builder can modify the base criteria object, so that once {@link #finish()} is
     * called, it can no longer be modified.<br>
     * It is expected that this method be called at the beginning of every "with" builder mutator.
     */
    protected final void verifyNotFinalized()
    {
        if (finalized)
        {
            throw new IllegalStateException(MessageFormat.format("This < className={0} > is in a finalized building state and can no longer be modified.",
                                                                 getClass().getName()));
        }
    }
}
