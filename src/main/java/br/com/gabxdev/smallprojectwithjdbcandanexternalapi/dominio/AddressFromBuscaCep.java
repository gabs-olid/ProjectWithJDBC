package br.com.gabxdev.smallprojectwithjdbcandanexternalapi.dominio;

public record AddressFromBuscaCep(String cep, String logradouro, String bairro, String localidade, String estado, String regiao, String erro) {
}
