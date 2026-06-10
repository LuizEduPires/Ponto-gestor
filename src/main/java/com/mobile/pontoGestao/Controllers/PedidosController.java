package com.mobile.pontoGestao.Controllers;

import com.mobile.pontoGestao.Dtos.Request.PedidoRequest;
import com.mobile.pontoGestao.Dtos.Request.PedidoRequestUpdate;
import com.mobile.pontoGestao.Dtos.Response.PedidoResponse;
import com.mobile.pontoGestao.Enums.OrdenacaoPedido;
import com.mobile.pontoGestao.Enums.StatusPedido;
import com.mobile.pontoGestao.Enums.TipoPedido;
import com.mobile.pontoGestao.Services.PedidosService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/pedidos")
@RequiredArgsConstructor
public class PedidosController {

    private final PedidosService  pedidosService;

    @PostMapping
    public PedidoResponse criarPedido(@RequestBody @Valid PedidoRequest pedidoRequest) throws ExecutionException, InterruptedException {
        return pedidosService.criarPedido(pedidoRequest);
    }

    @GetMapping
    public List<PedidoResponse> listarPedidos(@RequestParam(required = false) StatusPedido statusPedido, @RequestParam(required = false) TipoPedido tipoPedido, @RequestParam(required = false) String titulo, @RequestParam(required = false) OrdenacaoPedido ordenacao) throws ExecutionException, InterruptedException {
        return pedidosService.verPedidos(statusPedido, tipoPedido, titulo, ordenacao);
    }

    @GetMapping("/{id}")
    public PedidoResponse verPedido(@PathVariable String id) throws ExecutionException, InterruptedException {
        return pedidosService.verPedido(id);
    }

    @PatchMapping("/{id}")
    public PedidoResponse atualizarPedido(@RequestBody PedidoRequestUpdate pedidoRequest, @PathVariable String id) throws ExecutionException, InterruptedException {
        return pedidosService.atualizarPedido(id, pedidoRequest);
    }
}
