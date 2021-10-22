import java.util.Random;
import java.util.Scanner;

import java.io.*;

public class Main {
    public static Scanner sc = new Scanner(System.in);

    public static void main(String[]args)throws Exception{
        dadosIniciais();
        int escolha = 0;
        //MENU RECURSIVO COM OPCAO DE PARADA SE NECESSARIO
        do{
            System.out.println("\nSistema:\n1 - Cadastrar prontuario\n2 - Alteracao das anotacoess\n"+
                               "3 - Excluir\n0 - Sair do sistema");
            System.out.print("\nQual opção desejada ->"); escolha = sc.nextInt();
            switch(escolha){
                case 1:
                    cadastrarProntuario();
                break;

                case 2:
                    updateAnotacao();
                break;

                case 3:
                    deletarUsuario();
                break;

                case 0:
                    System.out.println("\nsaiu do programa com sucesso");
                break;

                default:
                    System.out.println("\nEscolha uma opcao valida!");
                break;
            }
        }while(escolha != 0);
    }

    private static void dadosIniciais()throws Exception{
        RandomAccessFile raf;
        try{
            raf = new RandomAccessFile("arquivomestre.db", "r");
            lerArquivoMestre(raf);
        }catch(Exception e){
            for(int x = 0; x != 1; ){
                System.out.print("Qual o tamanho de m-> "); ArquivoMestre.m = sc.nextByte();
                if(ArquivoMestre.m <= 0) System.out.println("Por favor escolha um valor positivo e nao nulo!");
                else if(ArquivoMestre.m > 120) System.out.println("O sistema so aceita ate 120 caracteres para m!");
                else{
                    x = 1;
                }
            }

            raf = new RandomAccessFile("arquivomestre.db", "rw");
            escreverArquivoMestre(raf);
        }
    }

    private static void escreverArquivoMestre(RandomAccessFile raf)throws Exception{
        
        ArquivoMestre.total_registros = 0;
        Prontuario.tamanho_total_prontuario += ArquivoMestre.m;
        raf.writeInt(0);//total de registros
        raf.writeInt(0);//registros ativos
        raf.writeByte(ArquivoMestre.m);//tamanho das anotações
        raf.close();
    }

    private static void lerArquivoMestre(RandomAccessFile raf){
        try{
            ArquivoMestre.total_registros = raf.readInt();
            ArquivoMestre.registrosAtivos = raf.readInt();
            ArquivoMestre.m = raf.readByte();
            raf.close();
            //atualizar informações do prontuario
            Prontuario.tamanho_total_prontuario += ArquivoMestre.m;
        }catch(Exception e){}
    }

    private static void cadastrarProntuario()throws Exception{
        String nome = "";
        int cpf = -1;
        char sexo = ' ';
        String idade = "";

        sc.nextLine();
        for(int x = 0; x != 1;){
            System.out.print("Por favor digite o nome do paciente->"); nome = sc.nextLine();
            if(nome.length() < 20){
                x = 1;
            }else System.out.println("Por favor, abrevie o nome!");
        }
        for(int x = 0; x != 1;){
            System.out.print("Por favor digite a data de nascimento do paciente no formato (01/01/2000)->"); idade = sc.nextLine();
            if(nome.length() < 10){
                x = 1;
            }else System.out.println("Por favor, digite um valor correto!");
        }
        for(int x = 0; x != 1;){
            System.out.print("Por favor digite o cpf do paciente->"); cpf = sc.nextInt();
            if((cpf >= 1 && cpf <= 999999999)){
                x = 1;
            }else System.out.println("Por favor, digite um cpf válido!");
        }
        for(int x = 0; x != 1;){
            System.out.print("Por favor digite o sexo do paciente(Ex: M(masculino) ou F(feminino))->"); sexo = (char)System.in.read();
            if((sexo == 'M' || sexo == 'F') || (sexo == 'm' || sexo == 'f')){
                x = 1;
                sexo = Character.toUpperCase(sexo);
            }else System.out.println("Por favor, digite um valor válido válido!");
        }
        double start = System.currentTimeMillis();
        Prontuario p = new Prontuario(nome, cpf, sexo, false, idade);
        p.write(ArquivoMestre.total_registros++); ArquivoMestre.registrosAtivos++;
        // atualiza o arquivo mestre
        RandomAccessFile r = new RandomAccessFile("arquivomestre.db", "rw");
        r.writeInt(ArquivoMestre.total_registros);
        r.writeInt(ArquivoMestre.registrosAtivos);
        r.close();
        System.out.println("Tempo para execução: "+((System.currentTimeMillis()-start)/1000)+" segundos");
    }

    //funcao de deletar, mudar depois da vinda do arquivo indice
    private static void deletarUsuario()throws Exception{
        int cpf = -1;
        System.out.print("Por favor digite o cpf do paciente->"); cpf = sc.nextInt();
        for(int i = 0; i < ArquivoMestre.total_registros; i++){
            Prontuario temp = new Prontuario();
            temp.read(i);
            if(temp.getCpf() == cpf){
                temp.setDeletado(true);
                double start = System.currentTimeMillis();
                temp.write(i);
                ArquivoMestre.registrosAtivos--;
                RandomAccessFile r = new RandomAccessFile("arquivomestre.db", "rw");
                r.writeInt(ArquivoMestre.total_registros);
                r.writeInt(ArquivoMestre.registrosAtivos);
                r.close();
                System.out.println("Tempo para execução: "+((System.currentTimeMillis()-start)/1000)+" segundos");
                break;
            }
        }
    }

    private static void updateAnotacao()throws Exception{
        int cpf = -1;
        String anotacao = "";
        System.out.print("Por favor digite o cpf do paciente->"); cpf = sc.nextInt();
        for(int i = 0; i < ArquivoMestre.total_registros; i++){
            Prontuario temp = new Prontuario();
            temp.read(i);
            if(temp.getCpf() == cpf){

                sc.nextLine();
                for(int x = 0; x != 1;){
                    System.out.print("Por favor digite a anotacao do paciente de ate "+ArquivoMestre.m+" caracteres->"); anotacao = sc.nextLine();
                    if(anotacao.length() < ArquivoMestre.m){
                        x = 1;
                    }else System.out.println("Por favor, diminua a anotacao!");
                }

                temp.setAnotacao(anotacao);
                double start = System.currentTimeMillis();
                temp.write(i);
                ArquivoMestre.registrosAtivos--;
                System.out.println("Tempo para execução: "+((System.currentTimeMillis()-start)/1000)+" segundos");
                break;
            }
        }
    }
}

class ArquivoMestre{
    public static byte m = 0;
    public static int total_registros = 0;
    public static int registrosAtivos = 0;
    public static final byte cabecalho_arquivo_mestre = 9;

    ArquivoMestre(){}
}

class Prontuario{
    //Informações pessoais do paciente
    private String nome;//tenha tamanho 20(eu defini), alem de 2 bytes para indicar o tamanho da string
    private String anotacao = "";//2 bytes
    private int cpf = 0;//4bytes
    private char sexo = ' ';//2bytes
    private boolean deletado = false;//2bytes
    private String idade;//10 da idade, mais 2 da string
    public static short tamanho_total_prontuario = 44;

    public Prontuario(){} //CONSTRUTOR VAZIO

    public Prontuario(String nome, int cpf, char sexo, boolean deletado, String idade) {
        this.nome = nome;
        this.cpf = cpf;
        this.sexo = sexo;
        this.deletado = deletado;
        this.idade = idade;
    }

    //função que escre o novo usuario no arquivo mestre
    public void write(int position)throws Exception{//
        RandomAccessFile r = new RandomAccessFile("arquivomestre.db", "rw");
        try{
            r.seek((position * tamanho_total_prontuario) + ArquivoMestre.cabecalho_arquivo_mestre);
            r.write(getBytes());
        }catch (IOException error){
            System.out.println(error);
        }
        r.close();
    }

    //função para retornar um vetor de bytes com as informações
    public byte[] getBytes(){
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(buffer);
        try{
            out.writeChar(deletado ? '*':' ');
            out.writeInt(cpf);
            out.writeUTF(nome);
            out.writeUTF(anotacao);
            out.writeChar(sexo);
            out.writeUTF(idade);
            out.flush();
        }catch(IOException error){
            System.out.println("Algum erro ocorreu! = "+error);
        }
        return buffer.toByteArray();
    }

    //função para ler no arquivo mestre um registro especifico
    public void read(int posicao)throws Exception{
        RandomAccessFile r = new RandomAccessFile("arquivomestre.db", "r");
        int nbytes = 0;
        try{
            byte[] buffer = new byte[tamanho_total_prontuario];
            r.seek((posicao * tamanho_total_prontuario) + ArquivoMestre.cabecalho_arquivo_mestre);
            nbytes = r.read(buffer);
            if(nbytes > 0)
                setBytes(buffer);
        }catch(IOException error){
            System.out.println(error);
        }
        r.close();
    }

    //colocar valores lidos nos atributos do objeto criado
    public void setBytes(byte[] buffer){
        ByteArrayInputStream buff = new ByteArrayInputStream(buffer);
        DataInputStream in = new DataInputStream(buff);

        try{
            deletado = in.readChar() == '*'? true : false;
            cpf = in.readInt();
            nome = in.readUTF();
            anotacao = in.readUTF();
            sexo = in.readChar();
            idade = in.readUTF();
        }catch(IOException error){
            System.out.println(error);
        }
    }


    public String getNome() {
        return this.nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getAnotacao() {
        return this.anotacao;
    }

    public void setAnotacao(String anotacao) {
        this.anotacao = anotacao;
    }

    public int getCpf() {
        return this.cpf;
    }

    public void setCpf(int cpf) {
        this.cpf = cpf;
    }

    public char getSexo() {
        return this.sexo;
    }

    public void setSexo(char sexo) {
        this.sexo = sexo;
    }

    public boolean isDeletado() {
        return this.deletado;
    }

    public boolean getDeletado() {
        return this.deletado;
    }

    public void setDeletado(boolean deletado) {
        this.deletado = deletado;
    }

    public String getIdade() {
        return this.idade;
    }

    public void setIdade(String idade) {
        this.idade = idade;
    }

}
