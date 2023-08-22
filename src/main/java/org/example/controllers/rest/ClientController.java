package org.example.controllers.rest;

import lombok.RequiredArgsConstructor;
import org.example.dtos.link.IdLinkRequest;
import org.example.dtos.link.NewLinkRequest;
import org.example.service.LinkService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/client/v1")
@RequiredArgsConstructor
public class ClientController {

    private final LinkService linkService;

    @PostMapping("/links")
    public ResponseEntity<?> createLink(@RequestBody NewLinkRequest newLinkRequest, @RequestHeader String authorization ) {
        return linkService.createNewLink(newLinkRequest,authorization);
    }
    @GetMapping("/links")
    public ResponseEntity<?> getLinks(@RequestHeader String authorization ) {
        return linkService.getLinks(authorization);
    }
    @PutMapping("/links")
    public ResponseEntity<?> putLinks(@RequestBody IdLinkRequest idLinkRequest, @RequestHeader String authorization ) {
        return linkService.putLink(idLinkRequest,authorization);
    }
    @DeleteMapping("/links")
    public ResponseEntity<?> deleteLinks(@RequestBody IdLinkRequest idLinkRequest, @RequestHeader String authorization ) {
        return linkService.removeLink(idLinkRequest,authorization);
    }
}
