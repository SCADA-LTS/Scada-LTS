package org.scada_lts.workdomain.event;

import com.serotonin.mango.rt.event.EventInstance;

/**
 * Interface to manage EventExporters
 * Create one or more event Exporter to save ScadaLTS event to remote location
 *
 * @author  Radek Jajko
 * @version 1.0.0
 * @since   25-09-2018
 */
public interface EventExporter {

    // EventExport Codes //
    int DEFAULT = 1;
    int RABBIT_MQ = 2;
    int SCADA_AND_RABBBIT = 3;

    /**
     * Initialize exporter
     *
     * Initialize exporter class by establishing a connection
     * to remote event data store.
     */
    void initialize();

    /**
     * Terminate exporter
     *
     * Terminate connection with remote data store
     */
    void terminate();

    /**
     * Export Event message
     *
     * To selected event location route publish a message with event data.
     * @param eventInstance - Event instance to publish
     */
    void export(EventInstance eventInstance);

}
