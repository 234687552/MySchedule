1.�����������Adpte
r�����getView����ֱ��ʹ��return newItemView��context���Ļ�
@Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return new ItemView(mContext);
        }
�ᵼ���������⣺
	Item�Ļ�����ListView���¹������ʧЧ
ԭ�������ͨ����getVIew����log��
	Log.w("getView", position+","+view );
	����֪��������ItemVIewǰ��ĵ�ַ�ǲ�һ�µģ���null���***���������ǵ���ԭ��
���������
	��return֮ǰ���ж��Ƿ�Ϊnull��Ȼ��Ϊnull�Ļ���ʵ����һ��view����
@Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ItemView view= (ItemView) convertView;
            if (view==null){
                view=new ItemView(mContext);
            }
            Log.w("getView", position+","+view );
            return view;
        }

2.Motion.Event ������̷���