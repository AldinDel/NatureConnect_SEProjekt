package at.fhv.Event.domain.model.user;

public class Role {
    private Long _id;
    private String _code;

    public Role(Long id, String code) {
        _id = id;
        _code = code;
    }

    public Long get_id() {
        return _id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    public String get_code() {
        return _code;
    }

    public void set_code(String _code) {
        this._code = _code;
    }
}
