package com.mobile.pontoGestao.Controllers;

import com.mobile.pontoGestao.Dtos.Request.PedidoRequest;
import com.mobile.pontoGestao.Dtos.Request.PedidoRequestUpdate;
import com.mobile.pontoGestao.Dtos.Request.SenhaRequest;
import com.mobile.pontoGestao.Dtos.Response.PedidoResponse;
import com.mobile.pontoGestao.Enums.OrdenacaoPedido;
import com.mobile.pontoGestao.Enums.StatusPedido;
import com.mobile.pontoGestao.Services.PedidosService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/pedidos")
@RequiredArgsConstructor
public class PedidosController {

    private final PedidosService pedidosService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PedidoResponse criarPedido(@RequestBody @Valid PedidoRequest request)
            throws ExecutionException, InterruptedException {
        return pedidosService.criarPedido(request);
    }

    @GetMapping
    public List<PedidoResponse> listarPedidos(
            @RequestParam(required = false) StatusPedido statusPedido,
            @RequestParam(required = false) String titulo,
            @RequestParam(required = false) OrdenacaoPedido ordenacao
    ) throws ExecutionException, InterruptedException {
        return pedidosService.verPedidos(statusPedido, titulo, ordenacao);
    }

    @GetMapping("/{id}")
    public PedidoResponse verPedido(@PathVariable String id)
            throws ExecutionException, InterruptedException {
        return pedidosService.verPedido(id);
    }

    @GetMapping("/cliente/{idCliente}")
    public List<PedidoResponse> verPedidosPorCliente(@PathVariable String idCliente)
            throws ExecutionException, InterruptedException {
        return pedidosService.getPedidosByCliente(idCliente);
    }

    @PatchMapping("/{id}")
    public PedidoResponse atualizarPedido(
            @PathVariable String id,
            @RequestBody @Valid PedidoRequestUpdate request
    ) throws ExecutionException, InterruptedException {
        return pedidosService.atualizarPedido(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletarPedido(
            @PathVariable String id
    ) throws ExecutionException, InterruptedException {
        pedidosService.deletarPedidos(id);
    }
}