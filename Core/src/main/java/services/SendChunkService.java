package services;

import com.google.protobuf.ByteString;
import core.SendChunkServiceGrpc;
import core.Chunk;
import core.Request;
import datamodels.File;
import io.grpc.stub.StreamObserver;

import java.util.ArrayList;

public class SendChunkService extends SendChunkServiceGrpc.SendChunkServiceImplBase {
    private ArrayList<File> files;

    public SendChunkService(ArrayList files) {
        super();
        this.files = files;
    }

    @Override
    public void requestChunk(Request request, StreamObserver responseObserver){
        System.out.println("Received chunk request");
        String filename = request.getFilename();
        int index = request.getIndex();
        File file = null;
        for(File _file : files){
            if(_file.getFilename().equals(filename)){
                file = _file;
                break;
            }
        }
        if(file == null){
            System.out.println("Do not have file "+filename);
            responseObserver.onError(new Exception("Do not have file"+filename));
            return;
        }
        if(!file.hasChunkAt(index)){
            System.out.println("Do not have chunk "+index);
            responseObserver.onError(new Exception("Do not have chunk"+index));
            return;
        }
        int chunkSize = file.getChunkSize();
        if(index*chunkSize + chunkSize  > file.getSize()){
            chunkSize = file.getSize() - index * file.getChunkSize();
        }
        ByteString chunk = ByteString.copyFrom(file.getData(),index*file.getChunkSize(), chunkSize);
        Chunk response = Chunk.newBuilder().setData(chunk).build();
        System.out.println("Sending Filename: "+filename+" Chunk index: "+index);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
