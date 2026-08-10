# Uso de Inteligência Artificial

Durante o desenvolvimento deste desafio técnico, utilizei Inteligência Artificial como ferramenta de apoio ao desenvolvimento, revisão e validação da solução.

A IA foi utilizada principalmente para:
- auxiliar na estruturação de testes unitários;
- revisar e melhorar a cobertura dos testes;
- auxiliar na identificação e correção de erros encontrados durante a execução dos testes;
- revisar a implementação do serviço de liquidação e do relatório de liquidações;
- auxiliar na implementação e revisão de filtros, paginação e consultas;
- revisar mensagens de erro e validações;
- auxiliar na documentação do projeto;
- esclarecer dúvidas relacionadas a Java, Spring Boot, JUnit, Mockito, JPA, PostgreSQL e Maven;
- auxiliar na análise de mensagens de erro e logs apresentados durante o desenvolvimento.

## Prompt utilizado

O prompt utilizado como contexto geral para o desenvolvimento foi:

> Estou desenvolvendo um desafio técnico em Java 21 utilizando Spring Boot, Spring Data JPA, PostgreSQL, Flyway, JUnit 5 e Mockito.
>
> O sistema é uma plataforma de crédito que possui recebíveis, moedas, taxas de câmbio e liquidação de recebíveis.
>
> Preciso implementar e revisar uma solução seguindo boas práticas de engenharia de software, separação de responsabilidades, arquitetura em camadas, validações, tratamento adequado de exceções, testes unitários e documentação.
>
> A funcionalidade principal envolve:
>
> 1. Liquidar um recebível em sua própria moeda ou em uma moeda diferente.
> 2. Quando as moedas forem diferentes, utilizar a taxa de câmbio disponível.
> 3. Calcular o valor presente do recebível considerando taxa base, spread e prazo.
> 4. Impedir a liquidação de um recebível já liquidado.
> 5. Validar a existência do recebível e da moeda de pagamento.
> 6. Gerar um relatório de liquidações com filtros opcionais por período, cedente e moeda.
> 7. Implementar paginação e limites de tamanho da página.
> 8. Criar testes unitários abrangendo cenários de sucesso, validações e erros.
>
> Analise o código fornecido, explique os problemas encontrados antes de propor alterações e forneça soluções compatíveis com a estrutura existente do projeto. Não invente classes, métodos ou atributos que não existam no código fornecido. Quando houver dúvidas sobre a implementação existente, solicite o código correspondente.
>
> Para cada alteração proposta, priorize código simples, legível, testável e compatível com Java 21 e Spring Boot. Depois da implementação, analise os erros apresentados pelos testes e proponha correções específicas.

## Validação

As sugestões geradas pela IA foram revisadas e adaptadas durante o desenvolvimento.

As alterações foram executadas localmente e os testes automatizados foram utilizados para validar o comportamento da aplicação.

Ao final do desenvolvimento, a suíte de testes apresentou:

- **24 testes executados**
- **0 falhas**
- **0 erros**
- **0 testes ignorados**
- **BUILD SUCCESS**

A IA foi utilizada como ferramenta de apoio e não como substituição da análise, implementação, execução e validação do código pelo desenvolvedor.