package br.com.gabxdev.smallprojectwithjdbcandanexternalapi.domain;

public record AddressFromBuscaCep(String cep, String logradouro, String bairro, String localidade, String estado, String regiao, String erro) {
}
