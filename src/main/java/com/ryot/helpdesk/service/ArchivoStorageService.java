package com.ryot.helpdesk.service;


import com.ryot.helpdesk.dto.Ticket.ArchivoGuardadoDto;
import com.ryot.helpdesk.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.net.MalformedURLException;

@Service
public class ArchivoStorageService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    private static final Set<String> EXTENSIONES_PERMITIDAS = Set.of(
            "pdf",
            "png",
            "jpg",
            "jpeg",
            "docx",
            "xlsx",
            "txt"
    );

    public ArchivoGuardadoDto guardarArchivoTicket(Long ticketId, MultipartFile archivo) {
        validarArchivo(archivo);

        String nombreOriginal = limpiarNombreArchivo(
                Objects.requireNonNull(archivo.getOriginalFilename())
        );

        String extension = ObtenerExtension(nombreOriginal);
        validarExtension(extension);

        String nombreArchivo = UUID.randomUUID() + "-" + nombreOriginal;

        try {
            Path carpetaTicket = Paths.get(uploadDir, "tickets", String.valueOf(ticketId))
                    .toAbsolutePath()
                    .normalize();

            Files.createDirectories(carpetaTicket);

            Path rutaDestino = carpetaTicket.resolve(nombreArchivo).normalize();

            if (!rutaDestino.startsWith(carpetaTicket)) {
                throw new BusinessException("Ruta de archivo no válida.");
            }

            Files.copy(
                    archivo.getInputStream(),
                    rutaDestino,
                    StandardCopyOption.REPLACE_EXISTING
            );

            return new ArchivoGuardadoDto(
                    nombreOriginal,
                    nombreArchivo,
                    rutaDestino.toString(),
                    archivo.getContentType(),
                    archivo.getSize()
            );

        } catch (IOException e) {
            throw new BusinessException("No se pudo guardar el archivo adjunto.");
        }
    }
    private void validarArchivo(MultipartFile archivo) {
        if(archivo == null || archivo.isEmpty()){
            throw new BusinessException("Debe seleccionar un archivo.");
        }
    }

    private String limpiarNombreArchivo(String nombreOriginal) {
        String nombre = Paths.get(nombreOriginal).getFileName().toString();

        if(nombre.contains("..")){
            throw new BusinessException("El nombre del archivo no sirve.!!!");
        }
        return nombre;
    }

    private String ObtenerExtension(String nombreArchivo) {
        int ultimoPunto = nombreArchivo.lastIndexOf(".");
        if(ultimoPunto == -1 || ultimoPunto == nombreArchivo.length() - 1){
            throw  new BusinessException("El archivo debe tener una extensioón valida");
        }
        return nombreArchivo.substring(ultimoPunto + 1).toLowerCase();

    }
    private void validarExtension(String extension) {
        if(!EXTENSIONES_PERMITIDAS.contains(extension)){
            throw new BusinessException("Tipo de archivo no permitido");
        }
    }
    public Resource cargarArchivo(String rutaArchivo) {
        try {
            Path baseDir = Paths.get(uploadDir)
                    .toAbsolutePath()
                    .normalize();

            Path ruta = Paths.get(rutaArchivo)
                    .toAbsolutePath()
                    .normalize();

            if (!ruta.startsWith(baseDir)) {
                throw new BusinessException("Ruta de archivo no permitida.");
            }

            Resource resource = new UrlResource(ruta.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                throw new BusinessException("El archivo no existe o no se puede leer.");
            }

            return resource;

        } catch (MalformedURLException e) {
            throw new BusinessException("La ruta del archivo no es válida.");
        }
    }
}
