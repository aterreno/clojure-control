(ns #^{:doc "A set of DSL for ssh, inspired by Fabric"
       :author "Sun Ning <classicning@gmail.com>  Dennis Zhuang<killme2008@gmail.com>"}
  control.commands)

(def SEP " ; ")
(defmacro path
  "modify shell path"
  [new-path & cmd]
  `(str "export PATH=" ~new-path ":$PATH"  SEP ~@cmd))

(defmacro cd 
  "change current directory"
  [path & cmd]
  `(str "cd " ~path SEP ~@cmd))

(defmacro prefix 
  "execute a prefix command, for instance, activate shell profile"
  [pcmd & cmd]  
  `(str ~pcmd " && " ~@cmd))

(defmacro env 
  "declare a env variable for next command"
  [key val & cmd]
  `(str ~key "=" ~val " " ~@cmd))

(defn run
  "simply run several commands"
  [ & cmds]
  (let [rt  (apply str cmds)]
    (if (.endsWith rt SEP)
      rt
      (str rt SEP))))

(defmacro sudo
  "run a command with sudo"
  [cmd]
  `(if (.endsWith ~cmd SEP)
     (str "sudo " ~cmd)
     (str "sudo " ~cmd SEP)))

(defn append
  "Append a line to a file"
  [file line & opts]
  (let [m (apply hash-map opts)
        escaple (:escaple m)
        sudo (:sudo m)]
    (if sudo
      (str "echo '" line "' | sudo tee -a " file SEP) 
      (str "echo '" line "' >> " file SEP))))

(defn sed-
  [file before after flags backup limit]
  (str "sed -i" backup " -r -e \"" limit " s/"  before "/" after "/" flags "\" " file SEP))

(defn sed
  "Use sed to replace strings matched pattern with options.Valid options include:
   :sudo   =>  true or false to use sudo,default is false.
   :flags   => sed options,default is nil.
   :limit    =>  sed limit,default is not limit.
   :backup  => backup file posfix,default is \".bak\"
   Equivalent to sed -i<backup> -r -e \"/<limit>/ s/<before>/<after>/<flags>g <filename>\"."

  [file before after & opts]
  (let [opts (apply hash-map opts)
        use-sudo (:sudo opts)
        flags (str (:flags opts) "g")
        backup (or (:backup opts) ".bak")
        limit (:limit opts)]
    (if use-sudo
      (sudo (sed- file before after flags backup limit))
      (sed- file before after flags backup limit))))

(defn  comm
  "Comments a line in a file with special character,default :char is \"#\"
   It use sed function to replace the line matched pattern, :sudo is also valid"
  [file pat & opts]
  (let [m (apply hash-map opts)
        char  (or (:char m) "#")]
    (apply sed file pat (str char "&") opts)))

(defn  uncomm
  "uncomment a line in a file"
  [file pat & opts]
  (let [m (apply hash-map opts)
        char  (or (:char m) "#")]
    (apply sed file (str "\\s*" char "+\\s*(" pat ")") "\\1" opts)))

(defn cat
  "cat a file"
  [file]
  (str "cat " file))

(defn chmod
  "chmod [mod] [file]"
  [mod file]
  (str "chmod " mod " " file SEP))

