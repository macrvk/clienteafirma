/* Copyright (C) 2011 [Gobierno de Espana]
 * This file is part of "Cliente @Firma".
 * "Cliente @Firma" is free software; you can redistribute it and/or modify it under the terms of:
 *   - the GNU General Public License as published by the Free Software Foundation;
 *     either version 2 of the License, or (at your option) any later version.
 *   - or The European Software License; either version 1.1 or (at your option) any later version.
 * Date: 11/01/11
 * You may contact the copyright holder at: soporte.afirma5@mpt.es
 */

package es.gob.afirma.core.signers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/** Factor&iacute;a que gestiona todos los formatos de firma disponibles en cada
 * momento en el cliente. */
public final class AOSignerFactory {

	private static final Logger LOGGER = Logger.getLogger("es.gob.afirma"); //$NON-NLS-1$

	private static final Map<String, AOSigner> SIGNERS = new HashMap<String, AOSigner>(20);

	/* Listado de los manejador de firma soportados y los identificadores de formato de firma asociados. */
	private static final String SIGNER_CLASS_CADES = "es.gob.afirma.signers.cades.AOCAdESSigner"; //$NON-NLS-1$
	private static final String SIGNER_CLASS_CADES_TRI = "es.gob.afirma.signers.cadestri.client.AOCAdESTriPhaseSigner"; //$NON-NLS-1$
	private static final String SIGNER_CLASS_CMS = "es.gob.afirma.signers.cms.AOCMSSigner"; //$NON-NLS-1$
	private static final String SIGNER_CLASS_FACTURAE = "es.gob.afirma.signers.xades.AOFacturaESigner"; //$NON-NLS-1$
	private static final String SIGNER_CLASS_XADES = "es.gob.afirma.signers.xades.AOXAdESSigner"; //$NON-NLS-1$
	private static final String SIGNER_CLASS_XADES_TRI = "es.gob.afirma.signers.xadestri.client.AOXAdESTriPhaseSigner"; //$NON-NLS-1$
	private static final String SIGNER_CLASS_XMLDSIG = "es.gob.afirma.signers.xmldsig.AOXMLDSigSigner"; //$NON-NLS-1$
	private static final String SIGNER_CLASS_PADES = "es.gob.afirma.signers.pades.AOPDFSigner"; //$NON-NLS-1$
	private static final String SIGNER_CLASS_PADES_TRI = "es.gob.afirma.signers.padestri.client.AOPDFTriPhaseSigner"; //$NON-NLS-1$
	private static final String SIGNER_CLASS_ODF = "es.gob.afirma.signers.odf.AOODFSigner"; //$NON-NLS-1$
	private static final String SIGNER_CLASS_OOXML = "es.gob.afirma.signers.ooxml.AOOOXMLSigner"; //$NON-NLS-1$

	// Listado los formatos con la siguiente informacion:
	// 0.- Nombre
	// 1.- Clase manejadora
	// 2.- Soporte de identificacion de firmas
	private static final String[][] SIGNERS_CLASSES = new String[][] {
		{AOSignConstants.SIGN_FORMAT_CADES, SIGNER_CLASS_CADES, Boolean.TRUE.toString()},
		{AOSignConstants.SIGN_FORMAT_CADES_TRI, SIGNER_CLASS_CADES_TRI, Boolean.FALSE.toString()},
		{AOSignConstants.SIGN_FORMAT_CMS, SIGNER_CLASS_CMS, Boolean.TRUE.toString()},
		{AOSignConstants.SIGN_FORMAT_FACTURAE, SIGNER_CLASS_FACTURAE, Boolean.TRUE.toString()},
		{AOSignConstants.SIGN_FORMAT_FACTURAE_ALT1, SIGNER_CLASS_FACTURAE, Boolean.FALSE.toString()},
		{AOSignConstants.SIGN_FORMAT_XADES, SIGNER_CLASS_XADES, Boolean.TRUE.toString()},
		{AOSignConstants.SIGN_FORMAT_XADES_TRI, SIGNER_CLASS_XADES_TRI, Boolean.TRUE.toString()},
		{AOSignConstants.SIGN_FORMAT_XADES_DETACHED, SIGNER_CLASS_XADES, Boolean.FALSE.toString()},
		{AOSignConstants.SIGN_FORMAT_XADES_ENVELOPED, SIGNER_CLASS_XADES, Boolean.FALSE.toString()},
		{AOSignConstants.SIGN_FORMAT_XADES_ENVELOPING, SIGNER_CLASS_XADES, Boolean.FALSE.toString()},
		{AOSignConstants.SIGN_FORMAT_XMLDSIG, SIGNER_CLASS_XMLDSIG, Boolean.TRUE.toString()},
		{AOSignConstants.SIGN_FORMAT_XMLDSIG_DETACHED, SIGNER_CLASS_XMLDSIG, Boolean.FALSE.toString()},
		{AOSignConstants.SIGN_FORMAT_XMLDSIG_ENVELOPED, SIGNER_CLASS_XMLDSIG, Boolean.FALSE.toString()},
		{AOSignConstants.SIGN_FORMAT_XMLDSIG_ENVELOPING, SIGNER_CLASS_XMLDSIG, Boolean.FALSE.toString()},
		{AOSignConstants.SIGN_FORMAT_PDF, SIGNER_CLASS_PADES, Boolean.FALSE.toString()},
		{AOSignConstants.SIGN_FORMAT_PDF_TRI, SIGNER_CLASS_PADES_TRI, Boolean.FALSE.toString()},
		{AOSignConstants.SIGN_FORMAT_PADES, SIGNER_CLASS_PADES, Boolean.TRUE.toString()},
		{AOSignConstants.SIGN_FORMAT_PADES_TRI, SIGNER_CLASS_PADES_TRI, Boolean.FALSE.toString()},
		{AOSignConstants.SIGN_FORMAT_ODF, SIGNER_CLASS_ODF, Boolean.TRUE.toString()},
		{AOSignConstants.SIGN_FORMAT_ODF_ALT1, SIGNER_CLASS_ODF, Boolean.FALSE.toString()},
		{AOSignConstants.SIGN_FORMAT_OOXML, SIGNER_CLASS_OOXML, Boolean.TRUE.toString()},
		{AOSignConstants.SIGN_FORMAT_OOXML_ALT1, SIGNER_CLASS_OOXML, Boolean.FALSE.toString()}
	};

	private AOSignerFactory() {
		// No permitimos la instanciacion externa
	}

	/** Recupera un manejador de firma capaz de tratar la firma indicada. En caso
	 * de no tener ning&uacute;n manejador compatible se devolver&aacute; <code>null</code>.
	 * @param signData Firma electr&oacute;nica
	 * @return Manejador de firma
	 * @throws IOException Si ocurren problemas relacionados con la lectura de la firma */
	public static AOSigner getSigner(final byte[] signData) throws IOException {
		if (signData == null) {
			throw new IllegalArgumentException("No se han indicado datos de firma"); //$NON-NLS-1$
		}
		for (final String format[] : SIGNERS_CLASSES) {

			// Solo buscaremos el signer compatible entre los que soportan la identificacion
			if (!Boolean.parseBoolean(format[2])) {
				continue;
			}

			if (SIGNERS.get(format[0]) == null) {
				try {
					SIGNERS.put(format[0], (AOSigner) Class.forName(format[1]).newInstance());
				}
				catch(final Exception e) {
					LOGGER.warning("No se ha podido instanciar un manejador para el formato de firma '" + format[0] + "': " + e); //$NON-NLS-1$ //$NON-NLS-2$
					continue;
				}
			}
			final AOSigner signer = SIGNERS.get(format[0]);
			if (signer != null && signer.isSign(signData)) {
				return signer;
			}
		}
		return null;
	}

	/** Obtiene un manejador para un formato de firma dado, o <code>null</code> en caso de no
	 * encontrar ninguno.
	 * @param signFormat Formato de firma para el cual solicitamos el manejador.
	 * @return Manejador capaz de firmar en el formato indicado. */
	public static AOSigner getSigner(final String signFormat) {
		String signerClass = null;
		for (final String[] format : SIGNERS_CLASSES) {
			if (format[0].equalsIgnoreCase(signFormat)) {
				signerClass = format[1];
				break;
			}
		}
		if (signerClass == null) {
			LOGGER.warning("El formato de firma '" + signFormat + "' no esta soportado, se devolvera null"); //$NON-NLS-1$ //$NON-NLS-2$
			return null;
		}
		if (SIGNERS.get(signFormat) == null) {
			try {
				SIGNERS.put(signFormat, (AOSigner) Class.forName(signerClass).newInstance());
			}
			catch(final Exception e) {
				LOGGER.severe("No se ha podido instanciar un manejador para el formato de firma '" + signFormat + "', se devolvera null: " + e); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		return SIGNERS.get(signFormat);
	}

	/**
	 * Recupera el listado de formatos de firma soportados por el Cliente (no los actualmente cargados).
	 * @return Listado de formatos.
	 */
	public static String[] getSupportedFormats() {
		int i = 0;
		final String[] formats = new String[SIGNERS_CLASSES.length];
		for (final String[] format : SIGNERS_CLASSES) {
			formats[i++] = format[0];
		}
		return formats;
	}

}
