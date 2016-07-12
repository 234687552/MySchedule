1.如果在适配器Adpte
r里面的getView里面直接使用return newItemView（context）的话
@Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return new ItemView(mContext);
        }
会导致以下问题：
	Item的滑动在ListView上下滚动后会失效
原因分析：通过在getVIew里面log出
	Log.w("getView", position+","+view );
	可以知道滚动后ItemVIew前后的地址是不一致的，从null变成***，估计这是导致原因
解决方法：
	在return之前先判断是否为null，然后为null的话先实例出一个view即可
@Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ItemView view= (ItemView) convertView;
            if (view==null){
                view=new ItemView(mContext);
            }
            Log.w("getView", position+","+view );
            return view;
        }

2.Motion.Event 点击过程发生