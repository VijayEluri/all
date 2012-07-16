package main

import (
	"bytes"
	"crypto/rand"
	"encoding/binary"
	"fmt"
	"math/big"
	"net"
	"os"
	"strconv"
	"time"
)

func error(str string) {
	fmt.Fprintf(os.Stderr, "%v\n", str)
}

func usage() {
	error("usage: domas_send_file [ip:port] [times]")
	os.Exit(2)
}

func generateFileName() (string, [260]byte) {
	var fileNameBytes [260]byte
	fileName := time.Now().Format("2006_01_02_15_04_05.dat")
	copy(fileNameBytes[:], []byte(fileName))
	return fileName, fileNameBytes
}

func generateFileSize() (int64, []byte) {
	buffer := new(bytes.Buffer)
	value, _ := rand.Int(rand.Reader, big.NewInt(1000))
	fileSize := (value.Int64() + 100) * 1040
	binary.Write(buffer, binary.LittleEndian, fileSize)
	return fileSize, buffer.Bytes()
}

func generateFileBuffer(size int64) []byte {
	buffer := new(bytes.Buffer)
	for i := int64(0); i < size; i += 8 {
		value, _ := rand.Int(rand.Reader, big.NewInt(0x7fffffffffffffff))
		binary.Write(buffer, binary.LittleEndian, value.Int64())
	}
	return buffer.Bytes()
}

func send(address string) int64 {
	conn, err := net.Dial("tcp", address)
	if err != nil {
		error("cannot connect to " + address)
		os.Exit(1)
	}

	fileName, fileNameBytes := generateFileName()
	fmt.Printf("file name: %v\n", fileName)

	fileSize, fileSizeBytes := generateFileSize()
	fmt.Printf("file size: %v, %v\n", fileSize, len(fileSizeBytes))

	fileBuffer := generateFileBuffer(fileSize)

	conn.Write(fileNameBytes[:])
	conn.Write(fileSizeBytes)
	conn.Write(fileBuffer)

	// this will wait for the previous write to complete on Windows
	conn.Close()

	return fileSize + int64(268)
}

func main() {
	if len(os.Args) < 2 {
		usage()
		os.Exit(1)
	}

	address := os.Args[1]
	times := 1
	if len(os.Args) > 2 {
		times, _ = strconv.Atoi(os.Args[2])
	}

	fmt.Printf("repeating %v times\n", times)

	startTime := time.Now()
	var totalSize int64

	for i := 0; i < times; i++ {
		fmt.Printf("%04d: sending file to %v\n", i + 1, address)
		totalSize += send(address)		
	}	

	duration := time.Now().Sub(startTime).Nanoseconds() / 1000000
	fmt.Printf("total size %v, duration %v ms, performance %v\n", totalSize, duration, totalSize / duration)

}
