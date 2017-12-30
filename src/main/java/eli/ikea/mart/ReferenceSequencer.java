package eli.ikea.mart;

import java.util.HashSet;
import java.util.Set;

import eli.veritas.Verifier;
import eli.veritas.exception.AssertionException;

/**
 * Sequencer to generate unique identifier values. These values are only guaranteed to be unique for the current instance and are not guaranteed to be
 * returned in any defined order.
 *
 * @author The Architect
 */
public class ReferenceSequencer
{
    private long      nextReferenceIdentifier;
    private int       bufferIncrements;
    private Set<Long> referenceSequence;

    private ReferenceSequencer(final int initialCapacity)
    {
        nextReferenceIdentifier = initialCapacity + 1;
        bufferIncrements = 1 + initialCapacity / 10;
        referenceSequence = new HashSet<>(initialCapacity);
        for (long refId = 1; refId < initialCapacity; refId++)
        {
            referenceSequence.add(refId);
        }
    }

    private ReferenceSequencer(final int initialCapacity, final Set<Long> excludedIdentifiers)
    {

    }

    /**
     * @param initialCapacity
     * @return
     * @throws AssertionException
     */
    public static ReferenceSequencer create(final int initialCapacity) throws AssertionException
    {
        Verifier.Inequality.assertGreaterThan("The initial capacity of the reference sequencer must be positive.", initialCapacity, 0);

        return new ReferenceSequencer(initialCapacity);
    }

    /**
     * @param initialCapacity
     * @param excludedIdentifiers
     * @return
     * @throws AssertionException
     */
    public static ReferenceSequencer create(final int initialCapacity, final Set<Long> excludedIdentifiers) throws AssertionException
    {
        Verifier.Inequality.assertGreaterThan("The initial capacity of the reference sequencer must be positive.", initialCapacity, 0);

        if (Verifier.Collections.isEmpty(excludedIdentifiers))
        {
            return new ReferenceSequencer(initialCapacity);
        }

        return new ReferenceSequencer(initialCapacity, excludedIdentifiers);
    }

    /**
     * @param bufferIncrements
     * @throws AssertionException
     */
    public void setBufferIncrements(final int bufferIncrements) throws AssertionException
    {
        Verifier.Inequality.assertGreaterThan("The buffer increments must be positive.", bufferIncrements, 0);

        this.bufferIncrements = bufferIncrements;
    }

    /**
     * @return the next value from the sequence.
     */
    public long peekNextReferenceIdentifier()
    {
        return referenceSequence.isEmpty() ? nextReferenceIdentifier : referenceSequence.iterator().next();
    }

    /**
     * @return and removes the next value from the reference sequence.
     */
    public long getNextReferenceIdentifier()
    {
        if (referenceSequence.isEmpty())
        {
            for (int refCounter = 0; refCounter < bufferIncrements; refCounter++, nextReferenceIdentifier++)
            {
                referenceSequence.add(nextReferenceIdentifier);
            }
        }
        final long referenceIdentifier = referenceSequence.iterator().next();
        referenceSequence.remove(referenceIdentifier);

        return referenceIdentifier;
    }

    /**
     * @param referenceIdentifier Restore reference identifier back into sequence.
     * @throws AssertionException If the specified reference identifier value is not between 0 and the current maximum reference identifier value.
     */
    public void restoreReferenceIdentifier(final long referenceIdentifier) throws AssertionException
    {
        Verifier.Ranges.assertInsideRange("Reference Identifier must be a previously used value.", referenceIdentifier, 0, nextReferenceIdentifier);

        if (!Verifier.Collections.containsValue(referenceSequence, referenceIdentifier))
        {
            referenceSequence.add(referenceIdentifier);
        }
    }
}
