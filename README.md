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


