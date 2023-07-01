package br.com.okfx.mrv.api.resource;

import br.com.okfx.mrv.api.event.RecursoCriadoEvent;
import br.com.okfx.mrv.api.model.InformacaoTributaria;
import br.com.okfx.mrv.api.repository.filter.InformacaoTributariaFilter;
import br.com.okfx.mrv.api.service.InformacaoTributariaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/informacaoTributaria")
public class InformacaoTributariaResource {

    @Autowired
    private InformacaoTributariaService service;

    @Autowired
    private ApplicationEventPublisher publisher;


    @GetMapping
    public List<InformacaoTributaria> listar(){
        return service.listarTodos();
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<InformacaoTributaria> buscarPeloCodigo(@PathVariable Long codigo) {
        InformacaoTributaria informacaoTributaria = service.buscarPorCodigo(codigo).get();
        return informacaoTributaria != null ? ResponseEntity.ok().body(informacaoTributaria) : ResponseEntity.notFound().build();
    }

    @GetMapping("/filtrar")
    public Page<InformacaoTributaria> pesquisar(InformacaoTributariaFilter informacaoTributariaFilter, Pageable pageable) {
        return service.filtrar(informacaoTributariaFilter, pageable);
    }

    @PostMapping
    public ResponseEntity<InformacaoTributaria> criar(@Valid @RequestBody InformacaoTributaria informacaoTributaria, HttpServletResponse response){
        InformacaoTributaria informacaoTributariaSalvo = service.salvar(informacaoTributaria);
        publisher.publishEvent((new RecursoCriadoEvent(this,response, informacaoTributariaSalvo.getId())));
        return ResponseEntity.status(HttpStatus.CREATED).body(informacaoTributariaSalvo);
    }

    @PutMapping("/{codigo}")
    public ResponseEntity<InformacaoTributaria> atualizar(@PathVariable Long codigo, @Valid @RequestBody InformacaoTributaria informacaoTributaria) {
        InformacaoTributaria informacaoTributariaSalva = service.atualizar(codigo, informacaoTributaria);
        return ResponseEntity.ok(informacaoTributariaSalva);
    }


    @DeleteMapping("/{codigo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> remover(@PathVariable Long codigo){
        service.apagar(codigo);
        return ResponseEntity.noContent().build();
    }



}
