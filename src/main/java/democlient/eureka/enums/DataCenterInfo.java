package democlient.eureka.enums;

public interface DataCenterInfo {
    enum Name {Netflix, Amazon, MyOwn}

    Name getName();
}
