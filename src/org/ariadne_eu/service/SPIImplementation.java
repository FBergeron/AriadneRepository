package org.ariadne_eu.service;

import org.apache.log4j.Logger;
import org.ariadne_eu.content.insert.InsertContentFactory;
import org.ariadne_eu.metadata.insert.InsertMetadataFactory;
import org.ariadne_eu.spi.CreateIdentifier;
import org.ariadne_eu.spi.CreateIdentifierResponse;
import org.ariadne_eu.spi.DeleteMetadataRecord;
import org.ariadne_eu.spi.DeleteResource;
import org.ariadne_eu.spi.FaultCodeType;
import org.ariadne_eu.spi.SPISkeleton;
import org.ariadne_eu.spi.SpiFault;
import org.ariadne_eu.spi.SpiFaultException;
//import org.ariadne_eu.spi.SpiFaultCodeType;
import org.ariadne_eu.spi.SubmitMetadataRecord;
import org.ariadne_eu.spi.SubmitResource;
import org.ariadne_eu.utils.config.ConfigManager;
import org.ariadne_eu.utils.config.RepositoryConstants;
import org.safehaus.uuid.EthernetAddress;
import org.safehaus.uuid.UUID;
import org.safehaus.uuid.UUIDGenerator;

import be.cenorm.www.SessionExpiredException;
import be.cenorm.www.Ticket;

/**
 * Created by ben
 * Date: 6-jan-2007
 * Time: 17:18:57
 * To change this template use File | Settings | File Templates.
 */
public class SPIImplementation extends SPISkeleton {
    private static Logger log = Logger.getLogger(SPIImplementation.class);

    public void deleteResource(DeleteResource deleteResource)
            throws SpiFaultException {
        log.info("deleteResource:identifier="+deleteResource.getGlobalIdentifier()+",sessionID="+deleteResource.getTargetSessionID());
        SpiFault fault = new SpiFault();
//        fault.setSpiFaultCode(SpiFaultCodeType.SPI_00000);
        fault.setSpiFaultCode(FaultCodeType.SPI_00000);
        fault.setMessage("Method not supported: deleteResource");
        SpiFaultException exception = new SpiFaultException();
        exception.setFaultMessage(fault);
        throw exception;
    }

    public void deleteMetadataRecord(DeleteMetadataRecord deleteMetadataRecord)
            throws SpiFaultException {
        log.info("deleteMetadataRecord:identifier="+deleteMetadataRecord.getGlobalIdentifier()+",sessionID="+deleteMetadataRecord.getTargetSessionID());
        SpiFault fault = new SpiFault();
//        fault.setSpiFaultCode(SpiFaultCodeType.SPI_00000);
        fault.setSpiFaultCode(FaultCodeType.SPI_00000);
        fault.setMessage("Method not supported: deleteMetadataRecord");
        SpiFaultException exception = new SpiFaultException();
        exception.setFaultMessage(fault);
        throw exception;
    }

    public CreateIdentifierResponse createIdentifier(CreateIdentifier createIdentifier)
            throws SpiFaultException {
        try {
            log.info("createIdentifier:sessionID="+createIdentifier.getTargetSessionID());
            Ticket ticket = Ticket.getTicket(createIdentifier.getTargetSessionID()); //throws exception if no valid ticket exists
            checkValidTicket(ticket);
            UUIDGenerator uuidGenerator = UUIDGenerator.getInstance();
            EthernetAddress ethernetAddress = uuidGenerator.getDummyAddress();
            UUID uuid = uuidGenerator.generateTimeBasedUUID(ethernetAddress);

            CreateIdentifierResponse response = new CreateIdentifierResponse();
            response.setLocalIdentifier(uuid.toString());
            log.info("createIdentifier:identifier="+response.getLocalIdentifier()+",sessionID="+createIdentifier.getTargetSessionID());
            return response;
        } catch (SessionExpiredException e) {
            log.debug("createIdentifier: ", e);
            SpiFault fault = new SpiFault();
//            fault.setSpiFaultCode(SpiFaultCodeType.SPI_00000);
            fault.setSpiFaultCode(FaultCodeType.SPI_00000);
            fault.setMessage("The given session ID is invalid");
            SpiFaultException exception = new SpiFaultException();
            exception.setFaultMessage(fault);
            throw exception;
        }
    }

    public void submitResource(SubmitResource submitResource)
            throws SpiFaultException {
        try {
            log.info("submitResource:identifier="+submitResource.getGlobalIdentifier()+",sessionID="+submitResource.getTargetSessionID());
            Ticket ticket = Ticket.getTicket(submitResource.getTargetSessionID()); //throws exception if no valid ticket exists
            checkValidTicket(ticket);

            /*boolean success = */InsertContentFactory.insertContent(submitResource.getGlobalIdentifier(), submitResource.getBinaryData().getBase64Binary(), "", "");
//            if (!success) {
//                log.warn("submitResource:identifier="+submitResource.getGlobalIdentifier()+",sessionID="+submitResource.getTargetSessionID()+ " submit failed");
//                SpiFault fault = new SpiFault();
//                fault.setSpiFaultCode(SpiFaultCodeType.SPI_00000);
//                fault.setMessage("Method not supported: submitResource");
//                SpiFaultException exception = new SpiFaultException();
//                exception.setFaultMessage(fault);
//                throw exception;
//            }
        } catch (SessionExpiredException e) {
            log.debug("submitResource: ", e);
            SpiFault fault = new SpiFault();
//            fault.setSpiFaultCode(SpiFaultCodeType.SPI_00000);
            fault.setSpiFaultCode(FaultCodeType.SPI_00000);
            fault.setMessage("The given session ID is invalid");
            SpiFaultException exception = new SpiFaultException();
            exception.setFaultMessage(fault);
            throw exception;
        }
    }

    public void submitMetadataRecord(SubmitMetadataRecord submitMetadataRecord)
            throws SpiFaultException {
        try {
            log.info("submitMetadataRecord:identifier="+submitMetadataRecord.getGlobalIdentifier()+",sessionID="+submitMetadataRecord.getTargetSessionID());
            Ticket ticket = Ticket.getTicket(submitMetadataRecord.getTargetSessionID()); //throws exception if no valid ticket exists
            checkValidTicket(ticket);
            InsertMetadataFactory.insertMetadata(submitMetadataRecord.getGlobalIdentifier(), submitMetadataRecord.getMetadata());
//        } catch (XMLDBException e) {
//            log.error("submitMetadataRecord: ", e);
        } catch (SessionExpiredException e) {
            log.debug("submitMetadataRecord: ", e);
            SpiFault fault = new SpiFault();
//            fault.setSpiFaultCode(SpiFaultCodeType.SPI_00000);
            fault.setSpiFaultCode(FaultCodeType.SPI_00000);
            fault.setMessage("The given session ID is invalid");
            SpiFaultException exception = new SpiFaultException();
            exception.setFaultMessage(fault);
            throw exception;
        }
    }
    
    public void setDataFormat(java.lang.String targetSessionID, java.lang.String dataFormatID) throws SpiFaultException {
    	
    }

    private void checkValidTicket(Ticket ticket) throws SpiFaultException {
        if (ticket.getParameter("username") == null ||
            !ticket.getParameter("username").equalsIgnoreCase(ConfigManager.getProperty(RepositoryConstants.REPO_USERNAME)) ||
            ticket.getParameter("password") == null ||
            !ticket.getParameter("password").equalsIgnoreCase(ConfigManager.getProperty(RepositoryConstants.REPO_PASSWORD))) {
            SpiFault fault = new SpiFault();
//            fault.setSpiFaultCode(SpiFaultCodeType.SPI_00000);
            fault.setSpiFaultCode(FaultCodeType.SPI_00000);
            fault.setMessage("The given session ID is invalid");
            SpiFaultException exception = new SpiFaultException();
            exception.setFaultMessage(fault);
            throw exception;
        }
    }

}
