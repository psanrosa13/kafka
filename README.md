# kafka
Projeto do curso de Kafka

### Usando o Kafka localmente :

Acesse https://kafka.apache.org/downloads e faça o download da versão mais recente.
Baixe o arquivo .targz e descompact ele.

#### Subir o Zookeeper: 
Acesse a pasta do kafka que você descompactou, entre na pasta e em seguida abra o terminal neste diretório.
Execute o comando no seu terminal para subir o Zookeeper:
 
  bin/zookeeper-server-start.sh config/zookeeper.properties

Ao final do log ele irá informar que se concetou a porta 2181.

#### Subir o Kafka:
 na mesma pasta abra o terminal e execute :

 bin/kafka-server-start.sh config/server.properties
 
 ##### Criar um tópico
 Abra outro terminal no mesmo diretório da pasta do kafka e digite o seguinte comando :
 
 bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic NEW_ORDER

*executando o comando  bin/kafka-topic.sh no terminal ele mostra todas as propriedades e comandos do topic*

#### Listar tópicos existentes

bin/kafka-topics.sh --list --bootstrap-server localhost:9092

#### Criar produtor de mensagens via linha de comando

Abra um terminal novo na pasta do kafka e execute este comando :

bin/kafka-console-producer.sh --broker-list localhost:9092 --topic NEW_ORDER

#### Consumir mensagens de um tópico

bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic NEW_ORDER

* este comando acima desconsidera as mensagens que já existiam e começa a ler a partir do momento que se registra como consumidor no tópico*

#### Consumir mensagens de um tópico desde o ínicio

bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic NEW_ORDER --from-beginning

#### Alterar a partição via linha de comando:

bin/kafka-topics.sh --alter --zookeeper localhost:2181 --topic ORDER_NEW --partitions 3

#### Detalhar grupos de consumo por linha de comando:

bin/kafka-consumer-groups.sh --bootstrap-server localhost:9092 --describe

consultar por consumidores por grupo.
bin/kafka-consumer-groups.sh --all-groups --bootstrap-server localhost:9092 --describe

###Subir duas instâncias de KAFKA

- Precisamos de outro arquivo de configuração.
- Existem várias formas de se fazer essa cópia, mas no exemplo vamos apenas copiar o arquivo da pasta config/server.properties e renomeá-lo 

cp config/server.properties config/server2.properties

- Depois editar o arquivo de configuração copiado e alterar algumas propriedades:

broker.id = 2

log.dirs= ( apontar para outro arq de log )

listeners = (alterar a porta )

####Para configurar o diretório dos dados das mensagens :

acesse o diretório config/ arquivo server.properties

Altere a propriedade logs.dirs

Necessário alterar a configuração do diretório de mensagens que esta no arquivo de configuração do Zookeerper

altere a propriedade dataDir do arquivo config/zookeeper.properties


## Anotações do que foi estudado:

------------------------------------------------------------------------------------------------
Um problema que devemos evitar é o single point of failure, para isso podemos subir várias intancias de kafka.

Como subir mais instancias de KAfka?

- Precisamos de outro arquivo de configuração.
- Existem várias formas de se fazer essa cópia, mas no exemplo vamos apenas copiar o arquivo da pasta config/server.properties e renomeá-lo 

comando :

cp config/server.properties config/server2.properties

- Depois editar o arquivo de configuração copiado e alterar algumas propriedades:
broker.id = 2

log.dirs= ( apontar para outro arq de log )

listeners = (alterar a porta )

Se fizermos o comando para descrever os tópicos :

Vamos perceber que o tópico esta configurado como replicationfactor zero, você irá perceber que no item replicas esta zero também e leader esta zero.

Para que considere várias instancias do Kafka é necessário que ele esteja configurado, para isso use o comando alterando esta opção de replicação do tópico:

bin/kafka-topics.sh --zookeeper localhost:2081 --alter --topic NEW_ORDER --partitions 3 --replication-factor 2

Porém para a propriedade fator de replicação não é possível alterar uma vez que o tópico ja esta criado.

é possível adicionar no arquivo de configuração do Kafka ( server.properties ) a propriedade deafult.replication.factor = 2.
Então se você tem mais de um arquivo de configuração do Kafka, deve fazer isso nos demais arquivos de server que tiver das intancias, claro que em cenários onde você tem muitas instâncias talvez seja interessante ter uma rotina automizada para fazer estas alterações.

Na descrição dos tópicos o item Isr é quais as replicas estão atualizadas até este instante.


### Propriedade:


offsets.topic.replication.factor = é a quantidade de replicações de uma mensagem, o recomendável e o que muitas empresas utilizam .

Se esta propriedade é alterada devem ser alteradas também as propriedades abaixo:

transaction.state.log.replication.factor = 3
deafult.replication.factor = 3


Quando você trabalha com replicas, quando o lider cai, outra instancia assume como líder.

Mas porque trabalhar com réplicas? Porque isso permite que você mantenha segurança, visto que você não terá um único ponto de falha , você terá mais pontos .

**** qual o papel do Leader no kafka ?

Se você envia a mensagem , chega a mensagem no leader, porém as réplicas caem, e quando as réplicas voltam quem cai é a leader que possuia a mensagem, o que acontece ? Primeiro que a mensagem continua na máquina que era lider, porém ao cair a líder, uma das réplicas vai assumir este lugar e ela estará com estado antigo, sem ter as informações recentes. 

Quando fazemos o produtor, ao realizar o send, retornamos um get para analisar o commit, neste caso, você pode configurar para que a confirmação do commit de entrega seja feito somente quando as replicas receberem, ao contrário disso você poderá tratar.

E qual é essa configuração ? ACKS

Nas proprierties do produtor você deverá setar a propriedade ProducerConfig.ACKS_CONFIG.
Valor 0 significa que não é necessário confirmar que as replicas receberam
Valor 1 significa que uma replica recebendo ele retorna ok
Valor all significa que quando todas as replicas receberem eu receberei o ok de envio da mensagem.

Quando queremos garantir que todas as mensagens estejam nas réplicas, é normal que isso tome mais tempo, porém esse é o porém de se ter garantia, conhecido mais como "reliability".
