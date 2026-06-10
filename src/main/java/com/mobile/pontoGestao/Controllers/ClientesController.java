package com.mobile.pontoGestao.Controllers;

import com.mobile.pontoGestao.Dtos.Request.ClienteRequest;
import com.mobile.pontoGestao.Dtos.Response.ClienteResponse;
import com.mobile.pontoGestao.Enums.OrdenacaoCliente;
import com.mobile.pontoGestao.Models.Clientes;
import com.mobile.pontoGestao.Services.ClientesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/clientes")
public class ClientesController {

    private final ClientesService clientesService;
    @PostMapping
    public ClienteResponse cadastrarCliente(@RequestBody @Valid ClienteRequest request){
        return clientesService.criarCliente(request);
    }

    @GetMapping
    public List<ClienteResponse> listarClientes(@RequestParam(required = false) String nome, @RequestParam(required = false) String telefone, @RequestParam(required = false) OrdenacaoCliente ordenacao) throws ExecutionException, InterruptedException {
        return clientesService.getClientes(nome, telefone, ordenacao);
    }

    @GetMapping("/{id}")
    public ClienteResponse buscarCliente(@PathVariable String id) throws ExecutionException, InterruptedException {
        return clientesService.getCliente(id);
    }

    @PatchMapping("/{id}")
    public ClienteResponse atualizarCliente(@PathVariable String id, @RequestBody ClienteRequest request) throws ExecutionException, InterruptedException {
        return clientesService.atualizarCliente(id,request);
    }
}
