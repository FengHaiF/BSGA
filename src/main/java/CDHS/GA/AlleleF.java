package CDHS.GA;

public class AlleleF {
    private int _id;
    private int _type;
    private String _name;

    public AlleleF() {
    }

    public AlleleF(int _id, int _type, String _name) {
        this._id = _id;
        this._type = _type;
        this._name = _name;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public int get_type() {
        return _type;
    }

    public void set_type(int _type) {
        this._type = _type;
    }

    public String get_name() {
        return _name;
    }

    public void set_name(String _name) {
        this._name = _name;
    }

    @Override
    public boolean equals(Object obj) {
        if(this==obj)
        {
            return true;
        }
        if(obj instanceof AlleleF)
        {
            AlleleF i = (AlleleF)obj;
            if(this.get_id()==i.get_id()&&this.get_type()==i.get_type())
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return get_id()*10+get_type();
    }
}
