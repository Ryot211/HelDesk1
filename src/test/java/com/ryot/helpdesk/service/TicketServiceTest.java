package com.ryot.helpdesk.service;

import com.ryot.helpdesk.dto.Ticket.ArchivoGuardadoDto;
import com.ryot.helpdesk.dto.Ticket.DashboardResumenDto;
import com.ryot.helpdesk.dto.Ticket.TicketAdjunto.TicketAdjuntoCrearDto;
import com.ryot.helpdesk.dto.Ticket.TicketAdjunto.TicketAjuntoDto;
import com.ryot.helpdesk.dto.Ticket.TicketAsignarDto;
import com.ryot.helpdesk.dto.Ticket.TicketComentario.ArchivoDescargaDto;
import com.ryot.helpdesk.dto.Ticket.TicketComentario.TicketComentarioCrearDto;
import com.ryot.helpdesk.dto.Ticket.TicketComentario.TicketComentarioDto;
import com.ryot.helpdesk.dto.Ticket.TicketCrearDto;
import com.ryot.helpdesk.dto.Ticket.TicketDto;
import com.ryot.helpdesk.dto.Ticket.TicketEstadoDto;
import com.ryot.helpdesk.dto.Ticket.TicketHistorialDto;
import com.ryot.helpdesk.dto.Ticket.TicketSolucionDto;
import com.ryot.helpdesk.entity.CategoriaTicket;
import com.ryot.helpdesk.entity.Departamento;
import com.ryot.helpdesk.entity.Ticket;
import com.ryot.helpdesk.entity.TicketAdjunto;
import com.ryot.helpdesk.entity.TicketComentario;
import com.ryot.helpdesk.entity.TicketHistorial;
import com.ryot.helpdesk.entity.Usuario;
import com.ryot.helpdesk.exception.BusinessException;
import com.ryot.helpdesk.mapper.TicketAdjuntoMapper;
import com.ryot.helpdesk.mapper.TicketComentarioMapper;
import com.ryot.helpdesk.mapper.TicketHistorialMapper;
import com.ryot.helpdesk.mapper.TicketMapper;
import com.ryot.helpdesk.repository.CategoriaTicketRepo;
import com.ryot.helpdesk.repository.DepartamentoRepo;
import com.ryot.helpdesk.repository.TicketAdjuntoRepo;
import com.ryot.helpdesk.repository.TicketComentarioRepo;
import com.ryot.helpdesk.repository.TicketHistorialRepo;
import com.ryot.helpdesk.repository.TicketRepo;
import com.ryot.helpdesk.repository.UsuarioRepo;
import com.ryot.helpdesk.utils.SisVars;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock
    private TicketRepo ticketRepo;
    @Mock
    private CategoriaTicketRepo categoriaTicketRepo;
    @Mock
    private DepartamentoRepo departamentoRepo;
    @Mock
    private UsuarioRepo usuarioRepo;
    @Mock
    private TicketMapper ticketMapper;
    @Mock
    private TicketComentarioRepo ticketComentarioRepo;
    @Mock
    private TicketComentarioMapper ticketComentarioMapper;
    @Mock
    private TicketAdjuntoRepo ticketAdjuntoRepo;
    @Mock
    private TicketAdjuntoMapper ticketAdjuntoMapper;
    @Mock
    private TicketHistorialRepo ticketHistorialRepo;
    @Mock
    private TicketHistorialMapper ticketHistorialMapper;
    @Mock
    private ArchivoStorageService archivoStorageService;

    @InjectMocks
    private TicketService ticketService;

    @Captor
    private ArgumentCaptor<Ticket> ticketCaptor;
    @Captor
    private ArgumentCaptor<TicketHistorial> historialCaptor;
    @Captor
    private ArgumentCaptor<TicketComentario> comentarioCaptor;
    @Captor
    private ArgumentCaptor<TicketAdjunto> adjuntoCaptor;

    @AfterEach
    void limpiarSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    // ---------- helpers ----------

    private static Usuario usuario(long id) {
        Usuario u = new Usuario();
        u.setId(id);
        return u;
    }

    private static CategoriaTicket categoria(long id) {
        CategoriaTicket c = new CategoriaTicket();
        c.setId(id);
        return c;
    }

    private static Departamento departamento(long id) {
        Departamento d = new Departamento();
        d.setId(id);
        return d;
    }

    private static Ticket ticketConEstado(String estado) {
        Ticket t = new Ticket();
        t.setEstado(estado);
        return t;
    }

    private static TicketCrearDto dtoCrearValido() {
        TicketCrearDto dto = new TicketCrearDto();
        dto.setTitulo("Falla impresora");
        dto.setDescripcion("No imprime");
        dto.setCategoriaId(1L);
        dto.setDepartamentoSolicitanteId(2L);
        dto.setCreadoPorId(3L);
        return dto;
    }

    private void mockGuardarTicketIdentidad() {
        when(ticketRepo.save(any(Ticket.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    // ---------- crear ----------

    @Nested
    class Crear {

        @Test
        void exito_generaCodigoRegistradoYRegistraHistorial() {
            TicketCrearDto dto = dtoCrearValido();
            when(categoriaTicketRepo.findById(1L)).thenReturn(Optional.of(categoria(1L)));
            when(departamentoRepo.findById(2L)).thenReturn(Optional.of(departamento(2L)));
            when(usuarioRepo.findById(3L)).thenReturn(Optional.of(usuario(3L)));
            when(ticketRepo.count()).thenReturn(0L);
            when(ticketRepo.existsByCodigo(anyString())).thenReturn(false);
            mockGuardarTicketIdentidad();

            ticketService.crear(dto);

            verify(ticketRepo).save(ticketCaptor.capture());
            Ticket guardado = ticketCaptor.getValue();
            int anio = LocalDate.now().getYear();
            assertThat(guardado.getCodigo()).isEqualTo(String.format("HD-%d-%06d", anio, 1));
            assertThat(guardado.getEstado()).isEqualTo(SisVars.REGISTRADO);

            verify(ticketHistorialRepo).save(historialCaptor.capture());
            TicketHistorial historial = historialCaptor.getValue();
            assertThat(historial.getAccion()).isEqualTo(SisVars.HIST_CREACION);
            assertThat(historial.getObservacion()).isEqualTo(SisVars.HIST_CREACION);
            assertThat(historial.getEstadoNuevo()).isEqualTo(SisVars.REGISTRADO);
            assertThat(historial.getEstadoAnteior()).isNull();
        }

        @Test
        void sinPrioridad_defaultMedia() {
            TicketCrearDto dto = dtoCrearValido();
            dto.setPrioridad(null);
            when(categoriaTicketRepo.findById(1L)).thenReturn(Optional.of(categoria(1L)));
            when(departamentoRepo.findById(2L)).thenReturn(Optional.of(departamento(2L)));
            when(usuarioRepo.findById(3L)).thenReturn(Optional.of(usuario(3L)));
            when(ticketRepo.count()).thenReturn(0L);
            when(ticketRepo.existsByCodigo(anyString())).thenReturn(false);
            mockGuardarTicketIdentidad();

            ticketService.crear(dto);

            verify(ticketRepo).save(ticketCaptor.capture());
            assertThat(ticketCaptor.getValue().getPrioridad()).isEqualTo("MEDIA");
        }

        @Test
        void codigoGeneradoYaExiste_incrementaSecuencial() {
            TicketCrearDto dto = dtoCrearValido();
            when(categoriaTicketRepo.findById(1L)).thenReturn(Optional.of(categoria(1L)));
            when(departamentoRepo.findById(2L)).thenReturn(Optional.of(departamento(2L)));
            when(usuarioRepo.findById(3L)).thenReturn(Optional.of(usuario(3L)));
            when(ticketRepo.count()).thenReturn(0L);
            when(ticketRepo.existsByCodigo(anyString())).thenReturn(true, false);
            mockGuardarTicketIdentidad();

            ticketService.crear(dto);

            verify(ticketRepo).save(ticketCaptor.capture());
            int anio = LocalDate.now().getYear();
            assertThat(ticketCaptor.getValue().getCodigo()).isEqualTo(String.format("HD-%d-%06d", anio, 2));
        }

        @Test
        void sinTitulo_lanzaExcepcion() {
            TicketCrearDto dto = dtoCrearValido();
            dto.setTitulo(null);
            assertThatThrownBy(() -> ticketService.crear(dto)).isInstanceOf(BusinessException.class);
        }

        @Test
        void sinDescripcion_lanzaExcepcion() {
            TicketCrearDto dto = dtoCrearValido();
            dto.setDescripcion(" ");
            assertThatThrownBy(() -> ticketService.crear(dto)).isInstanceOf(BusinessException.class);
        }

        @Test
        void sinCategoriaId_lanzaExcepcion() {
            TicketCrearDto dto = dtoCrearValido();
            dto.setCategoriaId(null);
            assertThatThrownBy(() -> ticketService.crear(dto)).isInstanceOf(BusinessException.class);
        }

        @Test
        void sinDepartamentoSolicitanteId_lanzaExcepcion() {
            TicketCrearDto dto = dtoCrearValido();
            dto.setDepartamentoSolicitanteId(null);
            assertThatThrownBy(() -> ticketService.crear(dto)).isInstanceOf(BusinessException.class);
        }

        @Test
        void sinCreadoPorId_lanzaExcepcion() {
            TicketCrearDto dto = dtoCrearValido();
            dto.setCreadoPorId(null);
            assertThatThrownBy(() -> ticketService.crear(dto)).isInstanceOf(BusinessException.class);
        }

        @Test
        void categoriaInexistente_lanzaExcepcion() {
            TicketCrearDto dto = dtoCrearValido();
            when(categoriaTicketRepo.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> ticketService.crear(dto)).isInstanceOf(BusinessException.class);
        }

        @Test
        void departamentoInexistente_lanzaExcepcion() {
            TicketCrearDto dto = dtoCrearValido();
            when(categoriaTicketRepo.findById(1L)).thenReturn(Optional.of(categoria(1L)));
            when(departamentoRepo.findById(2L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> ticketService.crear(dto)).isInstanceOf(BusinessException.class);
        }

        @Test
        void usuarioCreadorInexistente_lanzaExcepcion() {
            TicketCrearDto dto = dtoCrearValido();
            when(categoriaTicketRepo.findById(1L)).thenReturn(Optional.of(categoria(1L)));
            when(departamentoRepo.findById(2L)).thenReturn(Optional.of(departamento(2L)));
            when(usuarioRepo.findById(3L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> ticketService.crear(dto)).isInstanceOf(BusinessException.class);
        }
    }

    // ---------- asignar ----------

    @Nested
    class Asignar {

        @Test
        void exito() {
            TicketAsignarDto dto = new TicketAsignarDto();
            dto.setTicketId(10L);
            dto.setAsignadoId(5L);

            Ticket ticket = ticketConEstado(SisVars.REGISTRADO);
            Usuario asignado = usuario(5L);

            when(ticketRepo.findById(10L)).thenReturn(Optional.of(ticket));
            when(usuarioRepo.findById(5L)).thenReturn(Optional.of(asignado));
            mockGuardarTicketIdentidad();

            ticketService.asignar(dto);

            assertThat(ticket.getEstado()).isEqualTo(SisVars.ASIGNADO);
            assertThat(ticket.getFechaAsignacion()).isNotNull();
            assertThat(ticket.getUsuarioAsignado()).isEqualTo(asignado);

            verify(ticketHistorialRepo).save(historialCaptor.capture());
            TicketHistorial historial = historialCaptor.getValue();
            assertThat(historial.getAccion()).isEqualTo(SisVars.HIST_ASIGNACION);
            assertThat(historial.getEstadoAnteior()).isEqualTo(SisVars.REGISTRADO);
            assertThat(historial.getEstadoNuevo()).isEqualTo(SisVars.ASIGNADO);
        }

        @Test
        void ticketIdNulo_lanzaExcepcion() {
            TicketAsignarDto dto = new TicketAsignarDto();
            dto.setTicketId(null);
            dto.setAsignadoId(5L);

            assertThatThrownBy(() -> ticketService.asignar(dto)).isInstanceOf(BusinessException.class);
        }

        @Test
        void asignadoIdNulo_lanzaExcepcion() {
            TicketAsignarDto dto = new TicketAsignarDto();
            dto.setTicketId(10L);
            dto.setAsignadoId(null);

            assertThatThrownBy(() -> ticketService.asignar(dto)).isInstanceOf(BusinessException.class);
        }

        @Test
        void ticketInexistente_lanzaExcepcion() {
            TicketAsignarDto dto = new TicketAsignarDto();
            dto.setTicketId(10L);
            dto.setAsignadoId(5L);
            when(ticketRepo.findById(10L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> ticketService.asignar(dto)).isInstanceOf(BusinessException.class);
        }

        @Test
        void usuarioAsignadoInexistente_lanzaExcepcion() {
            TicketAsignarDto dto = new TicketAsignarDto();
            dto.setTicketId(10L);
            dto.setAsignadoId(5L);
            when(ticketRepo.findById(10L)).thenReturn(Optional.of(ticketConEstado(SisVars.REGISTRADO)));
            when(usuarioRepo.findById(5L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> ticketService.asignar(dto)).isInstanceOf(BusinessException.class);
        }
    }

    // ---------- cambiarEstado ----------

    @Nested
    class CambiarEstado {

        private TicketEstadoDto dto(Long ticketId, String estado) {
            TicketEstadoDto d = new TicketEstadoDto();
            d.setTicketId(ticketId);
            d.setEstado(estado);
            return d;
        }

        @Test
        void aEnProceso_fechaInicioAtencionNullSeSetea() {
            Ticket ticket = ticketConEstado(SisVars.ASIGNADO);
            ticket.setFechaInicioAtencion(null);
            when(ticketRepo.findById(1L)).thenReturn(Optional.of(ticket));
            mockGuardarTicketIdentidad();

            ticketService.cambiarEstado(dto(1L, SisVars.EN_PROCESO));

            assertThat(ticket.getEstado()).isEqualTo(SisVars.EN_PROCESO);
            assertThat(ticket.getFechaInicioAtencion()).isNotNull();
        }

        @Test
        void aEnProceso_fechaInicioAtencionYaSeteada_noSeSobreescribe() {
            LocalDateTime original = LocalDateTime.of(2020, 1, 1, 8, 0);
            Ticket ticket = ticketConEstado(SisVars.ASIGNADO);
            ticket.setFechaInicioAtencion(original);
            when(ticketRepo.findById(1L)).thenReturn(Optional.of(ticket));
            mockGuardarTicketIdentidad();

            ticketService.cambiarEstado(dto(1L, SisVars.EN_PROCESO));

            assertThat(ticket.getFechaInicioAtencion()).isEqualTo(original);
        }

        @Test
        void aResuelto_fechaResolucionSeSetea() {
            Ticket ticket = ticketConEstado(SisVars.EN_PROCESO);
            when(ticketRepo.findById(1L)).thenReturn(Optional.of(ticket));
            mockGuardarTicketIdentidad();

            ticketService.cambiarEstado(dto(1L, SisVars.RESUELTO));

            assertThat(ticket.getEstado()).isEqualTo(SisVars.RESUELTO);
            assertThat(ticket.getFechaResolucion()).isNotNull();
        }

        @Test
        void aCerrado_fechaCierreSeSetea() {
            Ticket ticket = ticketConEstado(SisVars.RESUELTO);
            when(ticketRepo.findById(1L)).thenReturn(Optional.of(ticket));
            mockGuardarTicketIdentidad();

            ticketService.cambiarEstado(dto(1L, SisVars.CERRADO));

            assertThat(ticket.getEstado()).isEqualTo(SisVars.CERRADO);
            assertThat(ticket.getFechaCierre()).isNotNull();
        }

        @Test
        void estadoNoPermitido_lanzaExcepcion() {
            assertThatThrownBy(() -> ticketService.cambiarEstado(dto(1L, "FOO")))
                    .isInstanceOf(BusinessException.class);

            verifyNoInteractions(ticketRepo);
        }

        @Test
        void ticketIdNulo_lanzaExcepcion() {
            assertThatThrownBy(() -> ticketService.cambiarEstado(dto(null, SisVars.EN_PROCESO)))
                    .isInstanceOf(BusinessException.class);
        }

        @Test
        void estadoNulo_lanzaExcepcion() {
            assertThatThrownBy(() -> ticketService.cambiarEstado(dto(1L, null)))
                    .isInstanceOf(BusinessException.class);
        }

        @Test
        void estadoBlank_lanzaExcepcion() {
            assertThatThrownBy(() -> ticketService.cambiarEstado(dto(1L, "  ")))
                    .isInstanceOf(BusinessException.class);
        }

        @Test
        void ticketInexistente_lanzaExcepcion() {
            when(ticketRepo.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> ticketService.cambiarEstado(dto(1L, SisVars.EN_PROCESO)))
                    .isInstanceOf(BusinessException.class);
        }
    }

    // ---------- cerrarConSolucion ----------

    @Nested
    class CerrarConSolucion {

        private TicketSolucionDto dtoValido() {
            TicketSolucionDto d = new TicketSolucionDto();
            d.setTicketId(1L);
            d.setCerradoPorId(2L);
            d.setSolucion("Se reinició el equipo");
            return d;
        }

        @Test
        void exito_estadoCerradoYFechaResolucionSeSeteaSiEraNull() {
            Ticket ticket = ticketConEstado(SisVars.EN_PROCESO);
            ticket.setFechaResolucion(null);
            Usuario cerradoPor = usuario(2L);
            when(ticketRepo.findById(1L)).thenReturn(Optional.of(ticket));
            when(usuarioRepo.findById(2L)).thenReturn(Optional.of(cerradoPor));
            mockGuardarTicketIdentidad();

            ticketService.cerrarConSolucion(dtoValido());

            assertThat(ticket.getEstado()).isEqualTo(SisVars.CERRADO);
            assertThat(ticket.getSolucion()).isEqualTo("Se reinició el equipo");
            assertThat(ticket.getFechaCierre()).isNotNull();
            assertThat(ticket.getFechaResolucion()).isNotNull();
        }

        @Test
        void fechaResolucionYaSeteada_noSeSobreescribe() {
            LocalDateTime original = LocalDateTime.of(2021, 5, 5, 10, 0);
            Ticket ticket = ticketConEstado(SisVars.EN_PROCESO);
            ticket.setFechaResolucion(original);
            when(ticketRepo.findById(1L)).thenReturn(Optional.of(ticket));
            when(usuarioRepo.findById(2L)).thenReturn(Optional.of(usuario(2L)));
            mockGuardarTicketIdentidad();

            ticketService.cerrarConSolucion(dtoValido());

            assertThat(ticket.getFechaResolucion()).isEqualTo(original);
        }

        @Test
        void ticketIdNulo_lanzaExcepcion() {
            TicketSolucionDto dto = dtoValido();
            dto.setTicketId(null);

            assertThatThrownBy(() -> ticketService.cerrarConSolucion(dto)).isInstanceOf(BusinessException.class);
        }

        @Test
        void cerradoPorIdNulo_lanzaExcepcion() {
            TicketSolucionDto dto = dtoValido();
            dto.setCerradoPorId(null);

            assertThatThrownBy(() -> ticketService.cerrarConSolucion(dto)).isInstanceOf(BusinessException.class);
        }

        @Test
        void solucionNula_lanzaExcepcion() {
            TicketSolucionDto dto = dtoValido();
            dto.setSolucion(null);

            assertThatThrownBy(() -> ticketService.cerrarConSolucion(dto)).isInstanceOf(BusinessException.class);
        }

        @Test
        void solucionBlank_lanzaExcepcion() {
            TicketSolucionDto dto = dtoValido();
            dto.setSolucion("   ");

            assertThatThrownBy(() -> ticketService.cerrarConSolucion(dto)).isInstanceOf(BusinessException.class);
        }

        @Test
        void ticketInexistente_lanzaExcepcion() {
            when(ticketRepo.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> ticketService.cerrarConSolucion(dtoValido())).isInstanceOf(BusinessException.class);
        }

        @Test
        void usuarioInexistente_lanzaExcepcion() {
            when(ticketRepo.findById(1L)).thenReturn(Optional.of(ticketConEstado(SisVars.EN_PROCESO)));
            when(usuarioRepo.findById(2L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> ticketService.cerrarConSolucion(dtoValido())).isInstanceOf(BusinessException.class);
        }
    }

    // ---------- agregarComentario ----------

    @Nested
    class AgregarComentario {

        private TicketComentarioCrearDto dtoValido() {
            TicketComentarioCrearDto d = new TicketComentarioCrearDto();
            d.setTicketId(1L);
            d.setUsuarioId(2L);
            d.setComentario("Estamos revisando el caso");
            return d;
        }

        @Test
        void exito_tipoExplicito() {
            TicketComentarioCrearDto dto = dtoValido();
            dto.setTipo(SisVars.INTERNO);
            Ticket ticket = ticketConEstado(SisVars.EN_PROCESO);
            Usuario usuario = usuario(2L);
            when(ticketRepo.findById(1L)).thenReturn(Optional.of(ticket));
            when(usuarioRepo.findById(2L)).thenReturn(Optional.of(usuario));
            when(ticketComentarioRepo.save(any(TicketComentario.class))).thenAnswer(inv -> inv.getArgument(0));

            ticketService.agregarComentario(dto);

            verify(ticketComentarioRepo).save(comentarioCaptor.capture());
            assertThat(comentarioCaptor.getValue().getTipo()).isEqualTo(SisVars.INTERNO);
        }

        @Test
        void tipoNull_defaultPublico() {
            TicketComentarioCrearDto dto = dtoValido();
            dto.setTipo(null);
            when(ticketRepo.findById(1L)).thenReturn(Optional.of(ticketConEstado(SisVars.EN_PROCESO)));
            when(usuarioRepo.findById(2L)).thenReturn(Optional.of(usuario(2L)));
            when(ticketComentarioRepo.save(any(TicketComentario.class))).thenAnswer(inv -> inv.getArgument(0));

            ticketService.agregarComentario(dto);

            verify(ticketComentarioRepo).save(comentarioCaptor.capture());
            assertThat(comentarioCaptor.getValue().getTipo()).isEqualTo(SisVars.PUBLICO);
        }

        @Test
        void tipoInvalido_lanzaExcepcion() {
            TicketComentarioCrearDto dto = dtoValido();
            dto.setTipo("FOO");

            assertThatThrownBy(() -> ticketService.agregarComentario(dto)).isInstanceOf(BusinessException.class);
        }

        @Test
        void ticketIdFaltante_lanzaExcepcion() {
            TicketComentarioCrearDto dto = dtoValido();
            dto.setTicketId(null);

            assertThatThrownBy(() -> ticketService.agregarComentario(dto)).isInstanceOf(BusinessException.class);
        }

        @Test
        void usuarioIdFaltante_lanzaExcepcion() {
            TicketComentarioCrearDto dto = dtoValido();
            dto.setUsuarioId(null);

            assertThatThrownBy(() -> ticketService.agregarComentario(dto)).isInstanceOf(BusinessException.class);
        }

        @Test
        void comentarioFaltante_lanzaExcepcion() {
            TicketComentarioCrearDto dto = dtoValido();
            dto.setComentario(" ");

            assertThatThrownBy(() -> ticketService.agregarComentario(dto)).isInstanceOf(BusinessException.class);
        }

        @Test
        void ticketInexistente_lanzaExcepcion() {
            when(ticketRepo.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> ticketService.agregarComentario(dtoValido())).isInstanceOf(BusinessException.class);
        }

        @Test
        void usuarioInexistente_lanzaExcepcion() {
            when(ticketRepo.findById(1L)).thenReturn(Optional.of(ticketConEstado(SisVars.EN_PROCESO)));
            when(usuarioRepo.findById(2L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> ticketService.agregarComentario(dtoValido())).isInstanceOf(BusinessException.class);
        }
    }

    // ---------- listarPorEstado ----------

    @Nested
    class ListarPorEstado {

        @Test
        void exito() {
            List<Ticket> tickets = List.of(ticketConEstado(SisVars.ASIGNADO));
            List<TicketDto> dtos = List.of(new TicketDto());
            when(ticketRepo.findByEstadoOrderByIdDesc(SisVars.ASIGNADO)).thenReturn(tickets);
            when(ticketMapper.toDtos(tickets)).thenReturn(dtos);

            List<TicketDto> resultado = ticketService.listarPorEstado(SisVars.ASIGNADO);

            assertThat(resultado).isEqualTo(dtos);
        }

        @Test
        void estadoNulo_lanzaExcepcion() {
            assertThatThrownBy(() -> ticketService.listarPorEstado(null)).isInstanceOf(BusinessException.class);
        }

        @Test
        void estadoBlank_lanzaExcepcion() {
            assertThatThrownBy(() -> ticketService.listarPorEstado("  ")).isInstanceOf(BusinessException.class);
        }
    }

    // ---------- listarComentarios ----------

    @Nested
    class ListarComentarios {

        @Test
        void exito() {
            List<TicketComentario> comentarios = List.of(new TicketComentario());
            List<TicketComentarioDto> dtos = List.of(new TicketComentarioDto());
            when(ticketComentarioRepo.findByTicketIdOrderByFechaCreacionAsc(1L)).thenReturn(comentarios);
            when(ticketComentarioMapper.toDto(comentarios)).thenReturn(dtos);

            List<TicketComentarioDto> resultado = ticketService.listarComentarios(1L);

            assertThat(resultado).isEqualTo(dtos);
        }

        @Test
        void ticketIdNulo_lanzaExcepcion() {
            assertThatThrownBy(() -> ticketService.listarComentarios(null)).isInstanceOf(BusinessException.class);
        }
    }

    // ---------- listarAdjuntos ----------

    @Nested
    class ListarAdjuntos {

        @Test
        void exito() {
            List<TicketAdjunto> adjuntos = List.of(new TicketAdjunto());
            List<TicketAjuntoDto> dtos = List.of(new TicketAjuntoDto());
            when(ticketAdjuntoRepo.findByTicketIdAndEstadoRegistroOrderByFechaCreacionDesc(1L, SisVars.Activo))
                    .thenReturn(adjuntos);
            when(ticketAdjuntoMapper.toDto(adjuntos)).thenReturn(dtos);

            List<TicketAjuntoDto> resultado = ticketService.listarAdjuntos(1L);

            assertThat(resultado).isEqualTo(dtos);
        }

        @Test
        void ticketIdNulo_lanzaExcepcion() {
            assertThatThrownBy(() -> ticketService.listarAdjuntos(null)).isInstanceOf(BusinessException.class);
        }
    }

    // ---------- listarHistorial ----------

    @Nested
    class ListarHistorial {

        @Test
        void exito() {
            List<TicketHistorial> historial = List.of(new TicketHistorial());
            List<TicketHistorialDto> dtos = List.of(new TicketHistorialDto());
            when(ticketHistorialRepo.findByTicketIdOrderByFechaCreacionDesc(1L)).thenReturn(historial);
            when(ticketHistorialMapper.toDto(historial)).thenReturn(dtos);

            List<TicketHistorialDto> resultado = ticketService.listarHistorial(1L);

            assertThat(resultado).isEqualTo(dtos);
        }

        @Test
        void ticketIdNulo_lanzaExcepcion() {
            assertThatThrownBy(() -> ticketService.listarHistorial(null)).isInstanceOf(BusinessException.class);
        }
    }

    // ---------- registrarAdjunto ----------

    @Nested
    class RegistrarAdjunto {

        private TicketAdjuntoCrearDto dtoValido() {
            TicketAdjuntoCrearDto d = new TicketAdjuntoCrearDto();
            d.setTicketId(1L);
            d.setSubidoPorId(2L);
            d.setNombreOriginal("captura.png");
            d.setNombreArchivo("uuid-captura.png");
            d.setRutaArchivo("/uploads/tickets/1/uuid-captura.png");
            return d;
        }

        @Test
        void exito() {
            Ticket ticket = ticketConEstado(SisVars.EN_PROCESO);
            Usuario subidoPor = usuario(2L);
            when(ticketRepo.findById(1L)).thenReturn(Optional.of(ticket));
            when(usuarioRepo.findById(2L)).thenReturn(Optional.of(subidoPor));
            when(ticketAdjuntoRepo.save(any(TicketAdjunto.class))).thenAnswer(inv -> inv.getArgument(0));

            ticketService.registrarAdjunto(dtoValido());

            verify(ticketAdjuntoRepo).save(adjuntoCaptor.capture());
            assertThat(adjuntoCaptor.getValue().getEstadoRegistro()).isEqualTo(SisVars.Activo);
            verify(ticketHistorialRepo).save(any(TicketHistorial.class));
        }

        @Test
        void ticketIdFaltante_lanzaExcepcion() {
            TicketAdjuntoCrearDto dto = dtoValido();
            dto.setTicketId(null);

            assertThatThrownBy(() -> ticketService.registrarAdjunto(dto)).isInstanceOf(BusinessException.class);
        }

        @Test
        void subidoPorIdFaltante_lanzaExcepcion() {
            TicketAdjuntoCrearDto dto = dtoValido();
            dto.setSubidoPorId(null);

            assertThatThrownBy(() -> ticketService.registrarAdjunto(dto)).isInstanceOf(BusinessException.class);
        }

        @Test
        void nombreOriginalFaltante_lanzaExcepcion() {
            TicketAdjuntoCrearDto dto = dtoValido();
            dto.setNombreOriginal(" ");

            assertThatThrownBy(() -> ticketService.registrarAdjunto(dto)).isInstanceOf(BusinessException.class);
        }

        @Test
        void nombreArchivoFaltante_lanzaExcepcion() {
            TicketAdjuntoCrearDto dto = dtoValido();
            dto.setNombreArchivo(null);

            assertThatThrownBy(() -> ticketService.registrarAdjunto(dto)).isInstanceOf(BusinessException.class);
        }

        @Test
        void rutaArchivoFaltante_lanzaExcepcion() {
            TicketAdjuntoCrearDto dto = dtoValido();
            dto.setRutaArchivo(" ");

            assertThatThrownBy(() -> ticketService.registrarAdjunto(dto)).isInstanceOf(BusinessException.class);
        }

        @Test
        void ticketInexistente_lanzaExcepcion() {
            when(ticketRepo.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> ticketService.registrarAdjunto(dtoValido())).isInstanceOf(BusinessException.class);
        }

        @Test
        void usuarioInexistente_lanzaExcepcion() {
            when(ticketRepo.findById(1L)).thenReturn(Optional.of(ticketConEstado(SisVars.EN_PROCESO)));
            when(usuarioRepo.findById(2L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> ticketService.registrarAdjunto(dtoValido())).isInstanceOf(BusinessException.class);
        }
    }

    // ---------- subirAdjunto ----------

    @Nested
    class SubirAdjunto {

        @Test
        void exito() {
            Ticket ticket = ticketConEstado(SisVars.EN_PROCESO);
            Usuario usuarioAutenticado = usuario(9L);
            MultipartFile archivo = new MockMultipartFile(
                    "file", "captura.png", "image/png", "contenido".getBytes());

            Authentication authMock = mock(Authentication.class);
            when(authMock.getName()).thenReturn("user@test.com");
            SecurityContextHolder.getContext().setAuthentication(authMock);

            when(ticketRepo.findById(1L)).thenReturn(Optional.of(ticket));
            when(usuarioRepo.findByEmailIgnoreCase("user@test.com")).thenReturn(Optional.of(usuarioAutenticado));
            when(archivoStorageService.guardarArchivoTicket(eq(1L), eq(archivo))).thenReturn(
                    new ArchivoGuardadoDto("captura.png", "uuid-captura.png",
                            "/uploads/tickets/1/uuid-captura.png", "image/png", 123L));
            when(ticketAdjuntoRepo.save(any(TicketAdjunto.class))).thenAnswer(inv -> inv.getArgument(0));

            ticketService.subirAdjunto(1L, archivo);

            verify(ticketAdjuntoRepo).save(adjuntoCaptor.capture());
            TicketAdjunto guardado = adjuntoCaptor.getValue();
            assertThat(guardado.getNombreOriginal()).isEqualTo("captura.png");
            assertThat(guardado.getNombreAchivo()).isEqualTo("uuid-captura.png");
            assertThat(guardado.getEstadoRegistro()).isEqualTo(SisVars.Activo);
            assertThat(guardado.getUsuario()).isEqualTo(usuarioAutenticado);
        }

        @Test
        void ticketInexistente_lanzaExcepcion() {
            MultipartFile archivo = new MockMultipartFile(
                    "file", "captura.png", "image/png", "contenido".getBytes());
            when(ticketRepo.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> ticketService.subirAdjunto(1L, archivo)).isInstanceOf(BusinessException.class);
        }

        @Test
        void usuarioAutenticadoNoEncontrado_lanzaExcepcion() {
            Ticket ticket = ticketConEstado(SisVars.EN_PROCESO);
            MultipartFile archivo = new MockMultipartFile(
                    "file", "captura.png", "image/png", "contenido".getBytes());

            Authentication authMock = mock(Authentication.class);
            when(authMock.getName()).thenReturn("desconocido@test.com");
            SecurityContextHolder.getContext().setAuthentication(authMock);

            when(ticketRepo.findById(1L)).thenReturn(Optional.of(ticket));
            when(usuarioRepo.findByEmailIgnoreCase("desconocido@test.com")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> ticketService.subirAdjunto(1L, archivo)).isInstanceOf(BusinessException.class);
            verify(archivoStorageService, never()).guardarArchivoTicket(anyLong(), any());
        }
    }

    // ---------- inactivarAdjunto ----------

    @Nested
    class InactivarAdjunto {

        @Test
        void exito_estadoRegistroInactivoSinTypo() {
            TicketAdjunto adjunto = new TicketAdjunto();
            adjunto.setTicket(new Ticket());
            adjunto.setUsuario(new Usuario());
            when(ticketAdjuntoRepo.findById(1L)).thenReturn(Optional.of(adjunto));

            ticketService.inactivarAdjunto(1L);

            assertThat(adjunto.getEstadoRegistro()).isEqualTo("INACTIVO");
            assertThat(adjunto.getEstadoRegistro()).isEqualTo(SisVars.INACTIVO);
            verify(ticketHistorialRepo).save(any(TicketHistorial.class));
            verify(ticketAdjuntoRepo).save(adjunto);
        }

        @Test
        void idNulo_lanzaExcepcion() {
            assertThatThrownBy(() -> ticketService.inactivarAdjunto(null)).isInstanceOf(BusinessException.class);
        }

        @Test
        void adjuntoInexistente_lanzaExcepcion() {
            when(ticketAdjuntoRepo.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> ticketService.inactivarAdjunto(1L)).isInstanceOf(BusinessException.class);
        }
    }

    // ---------- descargarAdjunto ----------

    @Nested
    class DescargarAdjunto {

        private TicketAdjunto adjuntoActivo(String rutaArchivo) {
            TicketAdjunto adjunto = new TicketAdjunto();
            adjunto.setEstadoRegistro(SisVars.Activo);
            adjunto.setRutaArchivo(rutaArchivo);
            adjunto.setNombreOriginal("captura.png");
            return adjunto;
        }

        @Test
        void exito_conTipoContenido() {
            TicketAdjunto adjunto = adjuntoActivo("/ruta/captura.png");
            adjunto.setTipoContenido("image/png");
            Resource resourceMock = mock(Resource.class);
            when(ticketAdjuntoRepo.findById(1L)).thenReturn(Optional.of(adjunto));
            when(archivoStorageService.cargarArchivo("/ruta/captura.png")).thenReturn(resourceMock);

            ArchivoDescargaDto resultado = ticketService.descargarAdjunto(1L);

            assertThat(resultado.getTipoContenido()).isEqualTo("image/png");
            assertThat(resultado.getNombreOriginal()).isEqualTo("captura.png");
            assertThat(resultado.getResource()).isEqualTo(resourceMock);
        }

        @Test
        void tipoContenidoNull_defaultOctetStream() {
            TicketAdjunto adjunto = adjuntoActivo("/ruta/captura.png");
            adjunto.setTipoContenido(null);
            when(ticketAdjuntoRepo.findById(1L)).thenReturn(Optional.of(adjunto));
            when(archivoStorageService.cargarArchivo("/ruta/captura.png")).thenReturn(mock(Resource.class));

            ArchivoDescargaDto resultado = ticketService.descargarAdjunto(1L);

            assertThat(resultado.getTipoContenido()).isEqualTo("application/octet-stream");
        }

        @Test
        void tipoContenidoBlank_defaultOctetStream() {
            TicketAdjunto adjunto = adjuntoActivo("/ruta/captura.png");
            adjunto.setTipoContenido("   ");
            when(ticketAdjuntoRepo.findById(1L)).thenReturn(Optional.of(adjunto));
            when(archivoStorageService.cargarArchivo("/ruta/captura.png")).thenReturn(mock(Resource.class));

            ArchivoDescargaDto resultado = ticketService.descargarAdjunto(1L);

            assertThat(resultado.getTipoContenido()).isEqualTo("application/octet-stream");
        }

        @Test
        void adjuntoIdNulo_lanzaExcepcion() {
            assertThatThrownBy(() -> ticketService.descargarAdjunto(null)).isInstanceOf(BusinessException.class);
        }

        @Test
        void adjuntoInexistente_lanzaExcepcion() {
            when(ticketAdjuntoRepo.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> ticketService.descargarAdjunto(1L)).isInstanceOf(BusinessException.class);
        }

        @Test
        void estadoRegistroNoActivo_lanzaExcepcion() {
            TicketAdjunto adjunto = adjuntoActivo("/ruta/captura.png");
            adjunto.setEstadoRegistro(SisVars.INACTIVO);
            when(ticketAdjuntoRepo.findById(1L)).thenReturn(Optional.of(adjunto));

            assertThatThrownBy(() -> ticketService.descargarAdjunto(1L)).isInstanceOf(BusinessException.class);
            verify(archivoStorageService, never()).cargarArchivo(anyString());
        }
    }

    // ---------- obtenerResumenDashboard ----------

    @Nested
    class ObtenerResumenDashboard {

        @Test
        void exito_mapeaContadoresCorrectos() {
            when(ticketRepo.count()).thenReturn(100L);
            when(ticketRepo.countByEstado(SisVars.REGISTRADO)).thenReturn(10L);
            when(ticketRepo.countByEstado(SisVars.ASIGNADO)).thenReturn(20L);
            when(ticketRepo.countByEstado(SisVars.CERRADO)).thenReturn(30L);
            when(ticketRepo.countByEstado(SisVars.RESUELTO)).thenReturn(40L);

            DashboardResumenDto resultado = ticketService.obtenerResumenDashboard();

            assertThat(resultado.getTotalTickets()).isEqualTo(100L);
            assertThat(resultado.getTicketsAbiertos()).isEqualTo(10L);
            assertThat(resultado.getTicketsEnAtencion()).isEqualTo(20L);
            assertThat(resultado.getTicketsCerrados()).isEqualTo(30L);
            assertThat(resultado.getTicketsFinalizados()).isEqualTo(40L);
        }
    }

    // ---------- listarTodos ----------

    @Nested
    class ListarTodos {

        @Test
        void exito() {
            List<Ticket> tickets = List.of(ticketConEstado(SisVars.REGISTRADO));
            List<TicketDto> dtos = List.of(new TicketDto());
            when(ticketRepo.findAllByOrderByIdDesc()).thenReturn(tickets);
            when(ticketMapper.toDtos(tickets)).thenReturn(dtos);

            List<TicketDto> resultado = ticketService.listarTodos();

            assertThat(resultado).isEqualTo(dtos);
        }
    }

    // ---------- buscarPorId ----------

    @Nested
    class BuscarPorId {

        @Test
        void exito() {
            Ticket ticket = ticketConEstado(SisVars.REGISTRADO);
            TicketDto dto = new TicketDto();
            when(ticketRepo.findById(1L)).thenReturn(Optional.of(ticket));
            when(ticketMapper.toDto(ticket)).thenReturn(dto);

            TicketDto resultado = ticketService.buscarPorId(1L);

            assertThat(resultado).isEqualTo(dto);
        }

        @Test
        void noExiste_lanzaExcepcion() {
            when(ticketRepo.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> ticketService.buscarPorId(1L)).isInstanceOf(BusinessException.class);
        }
    }
}
