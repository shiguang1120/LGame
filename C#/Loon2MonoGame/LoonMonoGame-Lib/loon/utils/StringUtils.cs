﻿using java.lang;
using System;

namespace loon.utils
{
    public sealed class StringUtils : CharUtils
    {
		public static string Format(string format,params object[] o)
		{
			if (CollectionUtils.IsEmpty(o))
			{
				return "";
			}
			StrBuilder b = new StrBuilder();
			int p = 0;
			for (; ; )
			{
				int i = format.IndexOf('{', p);
				if (i == -1)
				{
					break;
				}
				int idx = format.IndexOf('}', i + 1);
				if (idx == -1)
				{
					break;
				}
				if (p != i)
				{
					b.Append(format.Substring(p, i));
				}
				String nstr = format.Substring(i + 1, idx);
				try
				{
					int n = Integer.ParseInt(nstr);
					if (n >= 0 && n < o.Length)
					{
						b.Append(o[n]);
					}
					else
					{
						b.Append('{').Append(nstr).Append('}');
					}
				}
				catch (java.lang.Exception)
				{
					b.Append('{').Append(nstr).Append('}');
				}
				p = idx + 1;
			}
			b.Append(format.Substring(p));
			return b.ToString();
		}

		public static bool IsHex(CharSequence ch)
		{
			if (ch == null)
			{
				return false;
			}
			for (int i = 0; i < ch.Length(); i++)
			{
				int c = ch.CharAt(i);
				if (!IsHexDigit(c))
				{
					return false;
				}
			}
			return true;
		}
		public static bool IsHex(string ch)
		{
			if (ch == null)
			{
				return false;
			}
			for (int i = 0; i < ch.Length(); i++)
			{
				int c = ch.CharAt(i);
				if (!IsHexDigit(c))
				{
					return false;
				}
			}
			return true;
		}

		public static string[] Split(string str, char flag)
		{
			if (IsEmpty(str))
			{
				return new string[] { str };
			}
			int count = 0;
			int size = str.Length();
			for (int index = 0; index < size; index++)
			{
				if (flag == str.CharAt(index))
				{
					count++;
				}
			}
			if (str.CharAt(size - 1) != flag)
			{
				count++;
			}
			if (count == 0)
			{
				return new string[] { str };
			}
			int idx = -1;
			string[] strings = new string[count];
			for (int i = 0, len = strings.Length; i < len; i++)
			{
				int IndexStart = idx + 1;
				idx = str.IndexOf(flag, idx + 1);
				if (idx == -1)
				{
					strings[i] = str.Substring(IndexStart).Trim();
				}
				else
				{
					strings[i] = str.Substring(IndexStart, idx).Trim();
				}
			}
			return strings;
		}

		public static bool IsEmpty(string v)
        {
            return v == null || v.Length == 0 || "".Equals(v.Trim());
        }
        public static bool IsEmpty(params CharSequence[] v)
        {
            return v == null || v.Length == 0 || "".Equals(v.ToString().Trim());
        }
        public static bool IsEmpty(params string[] v)
        {
            return v == null || v.Length == 0 || "".Equals(v.ToString().Trim());
        }

        public static bool IsEmpty(params char[] v)
        {
            return v == null || v.Length == 0 || "".Equals(v.ToString().Trim());
        }


		public static string Join(char flag,params object[] o)
		{
			if (CollectionUtils.IsEmpty(o))
			{
				return "";
			}
			StrBuilder sbr = new StrBuilder();
			int size = o.Length;
			for (int i = 0; i < size; i++)
			{
				sbr.Append(o[i]);
				if (i < size - 1)
				{
					sbr.Append(flag);
				}
			}
			return sbr.ToString();
		}

		public static string Join(char flag,params float[] o)
		{
			if (CollectionUtils.IsEmpty(o))
			{
				return "";
			}
			StrBuilder sbr = new StrBuilder();
			int size = o.Length;
			for (int i = 0; i < size; i++)
			{
				sbr.Append(o[i]);
				if (i < size - 1)
				{
					sbr.Append(flag);
				}
			}
			return sbr.ToString();
		}

		public static string Join(char flag,params int[] o)
		{
			if (CollectionUtils.IsEmpty(o))
			{
				return "";
			}
			StrBuilder sbr = new StrBuilder();
			int size = o.Length;
			for (int i = 0; i < size; i++)
			{
				sbr.Append(o[i]);
				if (i < size - 1)
				{
					sbr.Append(flag);
				}
			}
			return sbr.ToString();
		}

		public static string Join(char flag,params long[] o)
		{
			if (CollectionUtils.IsEmpty(o))
			{
				return "";
			}
			StrBuilder sbr = new StrBuilder();
			int size = o.Length;
			for (int i = 0; i < size; i++)
			{
				sbr.Append(o[i]);
				if (i < size - 1)
				{
					sbr.Append(flag);
				}
			}
			return sbr.ToString();
		}

		public static string Join(char flag,params bool[] o)
		{
			if (CollectionUtils.IsEmpty(o))
			{
				return "";
			}
			StrBuilder sbr = new StrBuilder();
			int size = o.Length;
			for (int i = 0; i < size; i++)
			{
				sbr.Append(o[i]);
				if (i < size - 1)
				{
					sbr.Append(flag);
				}
			}
			return sbr.ToString();
		}

		public static string Join(char flag,params CharSequence[] o)
		{
			if (CollectionUtils.IsEmpty(o))
			{
				return "";
			}
			StrBuilder sbr = new StrBuilder();
			int size = o.Length;
			for (int i = 0; i < size; i++)
			{
				sbr.Append(o[i]);
				if (i < size - 1)
				{
					sbr.Append(flag);
				}
			}
			return sbr.ToString();
		}

		public static string Join(char flag, params string[] o)
		{
			if (CollectionUtils.IsEmpty(o))
			{
				return "";
			}
			StrBuilder sbr = new StrBuilder();
			int size = o.Length;
			for (int i = 0; i < size; i++)
			{
				sbr.Append(o[i]);
				if (i < size - 1)
				{
					sbr.Append(flag);
				}
			}
			return sbr.ToString();
		}
		public static string ToString(object o)
		{
			return o?.ToString();
		}
	}
}
