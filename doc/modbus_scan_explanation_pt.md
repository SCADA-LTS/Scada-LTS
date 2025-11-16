O serviço de scan Modbus é uma funcionalidade que permite descobrir "nós" (dispositivos) escravos em uma rede Modbus, seja ela serial ou IP. O processo é iniciado pela interface do usuário e executado no backend.

A seguir, detalho o fluxo de funcionamento:

### 1. Início na Interface do Usuário (Frontend)

*   **Página:** `WebContent/WEB-INF/jsp/dataSourceEdit/editModbus.jsp`
*   **Ação do Usuário:** O usuário clica no botão "Scan for nodes" (Pesquisar por nós).
*   **Gatilho:** Isso aciona a função JavaScript `scan()` dentro do arquivo JSP.

A função `scan()` faz o seguinte:
1.  Exibe uma mensagem de "Iniciando scan".
2.  Chama a função `scanImpl()`. Esta função está definida em `editModbusSerial.jsp` ou `editModbusIp.jsp`, dependendo do tipo de data source. Ela coleta os parâmetros de conexão (como porta serial, baud rate, endereço IP, etc.) da página.
3.  A função `scanImpl()` então faz uma chamada DWR (Direct Web Remoting) para o método `modbusSerialScan` ou `modbusIpScan` na classe Java `DataSourceEditDwr` no backend.

### 2. Execução no Backend

*   **Classe Principal:** `src/com/serotonin/mango/web/dwr/DataSourceEditDwr.java`
*   **Métodos:** `modbusSerialScan()` e `modbusIpScan()`

Esses métodos recebem os parâmetros de conexão da interface do usuário e fazem o seguinte:

1.  **Criação do `ModbusMaster`**: Com base nos parâmetros, um objeto `ModbusMaster` é criado. Este objeto é responsável pela comunicação com a rede Modbus. O `ModbusMaster` é configurado para o tipo de conexão específica (Serial ou IP).
2.  **Criação do `ModbusNodeScanListener`**: Um objeto `ModbusNodeScanListener` é instanciado. Este objeto é o núcleo da lógica de scan. Ele é executado em uma thread separada para não bloquear a interface do usuário.
3.  **Armazenamento na Sessão do Usuário**: A instância do `ModbusNodeScanListener` é armazenada na sessão do usuário. Isso permite que a interface do usuário consulte o status do scan posteriormente.

### 3. A Lógica do Scan (`ModbusNodeScanListener`)

A classe `ModbusNodeScanListener` (cuja existência é inferida a partir de `DataSourceEditDwr.java`) implementa a lógica de varredura:

1.  **Loop de Scan**: A classe itera através dos possíveis IDs de escravos Modbus (geralmente de 1 a 240).
2.  **Comando de Teste**: Para cada ID de escravo, ele usa o `ModbusMaster` para enviar uma solicitação de "Read Exception Status" (código de função Modbus 7). Este é um comando leve usado para verificar se um dispositivo está presente e respondendo.
3.  **Verificação de Resposta**:
    *   Se o dispositivo escravo responde à solicitação, seu ID é adicionado a uma lista de "nós encontrados".
    *   Se não houver resposta (dentro do timeout configurado), o listener simplesmente continua para o próximo ID.
4.  **Atualização de Status**: O listener mantém o controle do progresso (quantos IDs já foram verificados) e se o scan foi concluído ou cancelado.

### 4. Atualização da Interface do Usuário

Enquanto o scan está em execução no backend, a interface do usuário precisa ser atualizada com o progresso e os resultados.

*   **Polling**: A função JavaScript `scanUpdate()` no `editModbus.jsp` é chamada periodicamente (a cada segundo).
*   **Chamada DWR**: `scanUpdate()` chama o método `modbusScanUpdate()` na classe `DataSourceEditDwr`.
*   **Recuperação de Status**: O método `modbusScanUpdate()` recupera o objeto `ModbusNodeScanListener` da sessão do usuário e retorna um mapa de dados para o frontend, contendo:
    *   A lista de nós encontrados até o momento.
    *   Uma mensagem de status (ex: "50% concluído").
    *   Um indicador de que o scan foi finalizado.
*   **Exibição dos Resultados**: A função de callback `scanUpdateCB()` no JSP recebe esses dados e atualiza a lista de "Nós encontrados" e a mensagem de status na tela.

Quando o scan termina, o botão "Scan for nodes" é reativado, e o usuário pode ver a lista completa de dispositivos Modbus que foram encontrados na rede.

Em resumo, o processo é uma interação assíncrona entre a interface do usuário e o backend, onde a interface inicia um processo de longa duração no servidor e, em seguida, o consulta periodicamente para obter atualizações até que o processo seja concluído.
