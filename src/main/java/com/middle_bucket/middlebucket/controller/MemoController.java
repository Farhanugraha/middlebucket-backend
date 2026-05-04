package com.middle_bucket.middlebucket.controller;


import com.middle_bucket.middlebucket.dto.request.MemoRequest;
import com.middle_bucket.middlebucket.dto.response.ApiResponse;
import com.middle_bucket.middlebucket.dto.response.MemoResponse;
import com.middle_bucket.middlebucket.service.MemoService;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/memos")
public class MemoController {

    private final MemoService memoService;

    public MemoController(MemoService memoService){
        this.memoService = memoService;
    }

//      Get All Memos
    @GetMapping
    public ResponseEntity<ApiResponse<List<MemoResponse>>> getAllMemos(){
        return ResponseEntity.ok(ApiResponse.succes("Berhasih mengambil memos", memoService.getAllMemos()));
    }
//      Get Memo By ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MemoResponse>> getMemoById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(ApiResponse.succes("Berhasil",
                    memoService.getMemoById(id)));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

//    Post Memo
    @PostMapping
    public ResponseEntity<ApiResponse<MemoResponse>> createMemo(
            @RequestBody MemoRequest request,
            Authentication authentication){
        try{
            MemoResponse memo = memoService.createMemo(request, authentication.getName());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.succes("Memo Berhasil Dibuat", memo));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

//    Delete Memo
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMemo(@PathVariable Long id) {
        try {
            memoService.deleteMemo(id);
            return ResponseEntity.ok(ApiResponse.succes("Memo berhasil dihapus", null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

}
