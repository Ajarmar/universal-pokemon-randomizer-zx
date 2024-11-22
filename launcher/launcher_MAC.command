cd "$( dirname "$0" )"

if ! type -p java > /dev/null 2>&1; then
  echo "Java is not installed."

  echo "Press Enter to exit..."
  read -s
  exit
fi

if [[ ! $(java --version) == *"64-Bit"* ]]; then
  echo "Wrong Java version: No Java or Java 32-Bit instead of 64-Bit is installed."

  echo "Press Enter to exit..."
  read -s
  exit
fi

java -Xmx4608M -jar PokeRandoZX.jar please-use-the-launcher

echo "Press Enter to exit..."
read -s
